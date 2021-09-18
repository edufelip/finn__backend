import request from 'supertest'
import app from '../index'
import faker from 'faker'
import database from '../infra/database'
import fs from 'fs'

const fakeUser = {
  id: faker.datatype.uuid(),
  name: `${faker.name.firstName()} ${faker.name.lastName()}`
}

const fakeCommunity = {
  title: faker.lorem.word(5),
  description: faker.lorem.words(4),
  image: faker.image.imageUrl(),
  user_id: fakeUser.id
}

const fakePost = {
  content: faker.lorem.words(10),
  image: faker.image.imageUrl()
}

let fakeCommunityId: string|Object
let fakePostId: string|Object

describe('Posts Operations', () => {
  beforeAll(async () => {
    await database.query('CREATE TABLE users (id text primary key,name text not null,date timestamp default now());')
    await database.query('CREATE TABLE communities (id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, title VARCHAR(25) NOT NULL, description VARCHAR(100) NOT NULL, image TEXT, user_id TEXT, date TIMESTAMP DEFAULT now(), CONSTRAINT community_belongsTo_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE);')
    await database.query('CREATE TABLE userscommunities(id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, user_id TEXT, community_id INT, CONSTRAINT user_belongsToMany_communities FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE, CONSTRAINT community_belongsToMany_users FOREIGN KEY (community_id) REFERENCES communities (id) ON DELETE CASCADE );')
    await database.query('CREATE TABLE posts (id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, content TEXT NOT NULL, image TEXT, user_id TEXT, community_id INT, date TIMESTAMP DEFAULT now(), CONSTRAINT post_belongsTo_community FOREIGN KEY (community_id) REFERENCES communities (id) ON DELETE CASCADE, CONSTRAINT post_belongsTo_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE);')
    await database.query('CREATE TABLE likes (id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, user_id TEXT, post_id INT, CONSTRAINT user_likes_post FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE, CONSTRAINT post_has_likes FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE);')
  })
  beforeEach(async () => {
    await database.query('INSERT INTO users(id, name) VALUES(${id}, ${name})', fakeUser)
    fakeCommunityId = await database.query('INSERT INTO communities(title, description, image, user_id) VALUES(${title}, ${description}, ${image}, ${user_id}) RETURNING id', fakeCommunity)
    fakeCommunityId = fakeCommunityId[0].id
    fakePostId = await database.query('INSERT INTO posts(content, image, user_id, community_id) VALUES(${content}, ${image}, ${user_id}, ${community_id}) RETURNING id', { ...fakePost, user_id: fakeUser.id, community_id: fakeCommunityId })
    fakePostId = fakePostId[0].id
  })
  afterAll(async () => {
    await database.query('DROP TABLE likes')
    await database.query('DROP TABLE posts;')
    await database.query('DROP TABLE userscommunities;')
    await database.query('DROP TABLE communities;')
    await database.query('DROP TABLE users;')
    database.$pool.end()
  })
  afterEach(async () => {
    await database.query('DELETE FROM posts')
    await database.query('DELETE FROM userscommunities')
    await database.query('DELETE FROM communities;')
    await database.query('DELETE FROM users;')
  })

  it('Should get Feed', async () => {
    await database.query('INSERT INTO userscommunities(user_id, community_id) VALUES(${user_id}, ${community_id})',
      {
        user_id: fakeUser.id,
        community_id: fakeCommunityId
      })

    await request(app)
      .get(`/posts/users/${fakeUser.id}/feed`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              ...fakePost
            })
          ])
        )
      })
  })

  it('Should get Feed - Wrong Query Parameter', async () => {
    await database.query('INSERT INTO userscommunities(user_id, community_id) VALUES(${user_id}, ${community_id})',
      {
        user_id: fakeUser.id,
        community_id: fakeCommunityId
      })

    await request(app)
      .get(`/posts/users/${fakeUser.id}/feed?page=a`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              ...fakePost
            })
          ])
        )
      })
  })

  it('Should get Feed - Page 2', async () => {
    for (let i = 0; i < 20; i++) {
      await database.query('INSERT INTO posts(content, image, user_id, community_id) VALUES(${content}, ${image}, ${user_id}, ${community_id})',
        {
          content: `fake content ${i}`,
          image: '',
          user_id: fakeUser.id,
          community_id: fakeCommunityId
        })
    }
    await database.query('INSERT INTO userscommunities(user_id, community_id) VALUES(${user_id}, ${community_id})',
      {
        user_id: fakeUser.id,
        community_id: fakeCommunityId
      })

    await request(app)
      .get(`/posts/users/${fakeUser.id}/feed?page=2`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              content: 'fake content 5'
            })
          ])
        )
      })
  })

  it('Should get Posts from an user', async () => {
    await request(app)
      .get(`/posts/users/${fakeUser.id}`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              ...fakePost
            })
          ])
        )
      })
  })

  it('Should NOT get Posts from an user - Wrong User Id', async () => {
    await request(app)
      .get(`/posts/users/${fakeUser.id}1`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([])
        )
      })
  })

  it('Should get Posts from a community', async () => {
    await request(app)
      .get(`/posts/communities/${fakeCommunityId}`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              ...fakePost
            })
          ])
        )
      })
  })

  it('Should NOT get Posts from a Communiity - Wrong Community Id', async () => {
    await request(app)
      .get(`/posts/communities/${fakePostId}1`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([])
        )
      })
  })

  it('Should find a Post', async () => {
    await request(app)
      .get(`/posts/${fakePostId}`)
      .expect(200)
      .then(response => {
        expect(response.body).toMatchObject(fakePost)
      })
  })

  it('Should NOT find a Post', async () => {
    await request(app)
      .get(`/posts/${fakePostId}1`)
      .expect(404)
  })

  it('Should create a Post', async () => {
    const newPost = {
      content: 'new content',
      user_id: fakeUser.id,
      community_id: fakeCommunityId
    }

    await request(app)
      .post('/posts')
      .field('post', JSON.stringify(newPost))
      .attach('post', './src/test/testUtils/test.png')
      .expect(201)
      .then(response => {
        fs.unlink(`public/${response.body.image}`, (err) => {
          if (err) {
            console.log(err)
          }
        })
        expect(response.body).toMatchObject(newPost)
      })
  })

  it('Should update a Post - Change Content', async () => {
    const updatePost = { ...fakePost, content: 'random content' }

    await request(app)
      .put(`/posts/${fakePostId}`)
      .send(updatePost)
      .expect(204)

    await request(app)
      .get(`/posts/${fakePostId}`)
      .expect(200)
      .then(response => {
        expect(response.body).toMatchObject({ ...updatePost, id: fakePostId })
      })
  })

  it('Should update a Post - Change Image', async () => {
    const updatePost = { ...fakePost, image: 'random image url' }

    await request(app)
      .put(`/posts/${fakePostId}`)
      .send(updatePost)
      .expect(204)

    await request(app)
      .get(`/posts/${fakePostId}`)
      .expect(200)
      .then(response => {
        expect(response.body).toMatchObject({ ...updatePost, id: fakePostId })
      })
  })

  it('Should NOT update a Post - Invalid Id', async () => {
    const updatePost = { ...fakePost, content: 'random content' }

    await request(app)
      .put(`/posts/${fakePostId}1`)
      .send(updatePost)
      .expect(404)
  })

  it('Should delete a Post', async () => {
    await request(app)
      .delete(`/posts/${fakePostId}`)
      .expect(204)

    await request(app)
      .get(`/posts/${fakePostId}`)
      .expect(404)
  })

  it('Should give like to Post', async () => {
    await database.query('DELETE FROM likes')
    const payload = {
      user_id: fakeUser.id,
      post_id: fakePostId
    }

    await request(app)
      .post('/posts/likes')
      .send(payload)
      .expect(201)
      .then(response => {
        expect(response.body).toMatchObject(payload)
      })
  })

  it('Should remove like from Post', async () => {
    await database.query('DELETE FROM likes')
    const payload = {
      user_id: fakeUser.id,
      post_id: fakePostId
    }

    await request(app)
      .post('/posts/likes')
      .send(payload)

    await request(app)
      .delete(`/posts/${fakePostId}`)
      .send({ id: fakeUser.id })
      .expect(204)
  })

  it('Should get like count from Post', async () => {
    await database.query('DELETE FROM likes')
    const payload = {
      user_id: fakeUser.id,
      post_id: fakePostId
    }

    await request(app)
      .post('/posts/likes')
      .send(payload)

    await request(app)
      .get(`/posts/${fakePostId}/likes`)
      .expect(200)
      .then(response => {
        expect(response.body).toBe(1)
      })
  })
})
