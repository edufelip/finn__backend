import request from 'supertest'
import app from '../index'
import faker from 'faker'
import database from '../infra/database'

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

const fakeComment = {
  content: faker.lorem.words(10)
}

let fakeCommunityId: string|Object
let fakePostId: string|Object
let fakeCommentId: string|Object

describe.only('User Operations', () => {
  beforeAll(async () => {
    await database.query('CREATE TABLE users (id text primary key,name text not null,date timestamp default now());')
    await database.query('CREATE TABLE communities (id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, title VARCHAR(25) NOT NULL, description VARCHAR(100) NOT NULL, image TEXT, user_id TEXT, date TIMESTAMP DEFAULT now(), CONSTRAINT community_belongsTo_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE);')
    await database.query('CREATE TABLE posts (id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, content TEXT NOT NULL, image TEXT, user_id TEXT, community_id INT, date TIMESTAMP DEFAULT now(), CONSTRAINT post_belongsTo_community FOREIGN KEY (community_id) REFERENCES communities (id) ON DELETE CASCADE, CONSTRAINT post_belongsTo_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE);')
    await database.query('CREATE TABLE comments (id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, content VARCHAR(200) NOT NULL, user_id TEXT, post_id INT, date TIMESTAMP DEFAULT now(), CONSTRAINT comment_belongsTo_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE, CONSTRAINT comment_belongsTo_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE);')
  })
  beforeEach(async () => {
    await database.query('INSERT INTO users(id, name) VALUES(${id}, ${name})', fakeUser)
    fakeCommunityId = await database.query('INSERT INTO communities(title, description, image, user_id) VALUES(${title}, ${description}, ${image}, ${user_id}) RETURNING id', fakeCommunity)
    fakeCommunityId = fakeCommunityId[0].id
    fakePostId = await database.query('INSERT INTO posts(content, image, user_id, community_id) VALUES(${content}, ${image}, ${user_id}, ${community_id}) RETURNING id', { ...fakePost, user_id: fakeUser.id, community_id: fakeCommunityId })
    fakePostId = fakePostId[0].id
    fakeCommentId = await database.query('INSERT INTO comments(content, user_id, post_id) VALUES(${content}, ${user_id}, ${post_id}) RETURNING id', { ...fakeComment, user_id: fakeUser.id, post_id: fakePostId })
    fakeCommentId = fakeCommentId[0].id
  })
  afterAll(async () => {
    await database.query('DROP TABLE comments')
    await database.query('DROP TABLE posts;')
    await database.query('DROP TABLE communities;')
    await database.query('DROP TABLE users;')
    database.$pool.end()
  })
  afterEach(async () => {
    await database.query('DELETE FROM comments')
    await database.query('DELETE FROM posts')
    await database.query('DELETE FROM communities;')
    await database.query('DELETE FROM users;')
  })

  it('Should get a comment', async () => {
    await request(app)
      .get(`/comments/${fakeCommentId}`)
      .expect(200)
      .then(response => {
        expect(response.body).toMatchObject(fakeComment)
      })
  })

  it('Should NOT get a comment', async () => {
    await request(app)
      .get(`/comments/${fakeCommentId}1`)
      .expect(404)
  })

  it('Should get comments from a Post', async () => {
    await request(app)
      .get(`/comments/posts/${fakePostId}`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              ...fakeComment
            })
          ])
        )
      })
  })

  it('Should NOT get comments from a Post (Empty Array)', async () => {
    await request(app)
      .get(`/comments/posts/${fakePostId}1`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([
          ])
        )
      })
  })

  it('Should get comments from an User', async () => {
    await request(app)
      .get(`/comments/users/${fakeUser.id}`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              ...fakeComment
            })
          ])
        )
      })
  })

  it('Should NOT get comments from an User (Empty Array)', async () => {
    await request(app)
      .get(`/comments/users/${fakeUser.id}`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([])
        )
      })
  })

  it('should create a comment', async () => {
    const newComment = {
      content: faker.lorem.words(4),
      user_id: fakeUser.id,
      post_id: fakePostId
    }
    await request(app)
      .post('/comments')
      .send(newComment)
      .expect(201)
      .then(response => {
        expect(response.body).toMatchObject(newComment)
      })
  })

  it('should NOT create a comment - CONTENT TOO LONG', async () => {
    let newContent = ''
    for (let i = 0; i < 40; i++) {
      newContent += faker.lorem.word(4)
      newContent += ' '
    }
    const newComment = {
      content: newContent,
      user_id: fakeUser.id,
      post_id: fakePostId
    }
    await request(app)
      .post('/comments')
      .send(`${newComment} `)
      .expect(500)
  })

  it('should NOT create a comment - INVALID POST ID', async () => {
    const newComment = {
      content: 'Random Content',
      user_id: fakeUser.id,
      post_id: `${fakePostId}1`
    }
    await request(app)
      .post('/comments')
      .send(newComment)
      .expect(500)
  })

  it('should NOT create a comment - INVALID USER ID', async () => {
    const newComment = {
      content: 'Random Content',
      user_id: `${fakeUser.id}1`,
      post_id: fakePostId
    }
    await request(app)
      .post('/comments')
      .send(newComment)
      .expect(500)
  })

  it('should update a comment', async () => {
    // update it
    const newComment = {
      content: 'Random Content'
    }
    await request(app)
      .put(`/comments/${fakeCommentId}`)
      .send(newComment)
      .expect(204)

    // then get it
    await request(app)
      .get(`/comments/${fakeCommentId}`)
      .expect(200)
      .then(response => {
        expect(response.body).toMatchObject(newComment)
      })
  })

  it('should NOT update a comment', async () => {
    const newComment = {
      content: 'Random Content'
    }
    await request(app)
      .put(`/comments/${fakeCommentId}1`)
      .send(newComment)
      .expect(404)
  })

  it('should delete a comment', async () => {
    // delete
    await request(app)
      .delete(`/comments/${fakeCommentId}`)
      .expect(204)

    // then search
    await request(app)
      .get(`/comments/${fakeCommentId}`)
      .expect(404)
  })
})
