import request from 'supertest'
import app from '../index'
import faker from 'faker'
import database from '../infra/database'
import fs from 'fs'
import path from 'path'

const fakeUser = {
  id: faker.datatype.uuid(),
  name: `${faker.name.firstName()} ${faker.name.lastName()}`
}

let randomTitle = ''
for (let i = 0; i < 4; i++) {
  randomTitle += `${faker.lorem.word(5)} `
}
randomTitle += '.'

let randomDesc = ''
for (let i = 0; i < 16; i++) {
  randomDesc += `${faker.lorem.word(5)} `
}
randomDesc += faker.lorem.word(4)

const fakeCommunity = {
  title: randomTitle,
  description: randomDesc,
  image: faker.image.imageUrl(),
  user_id: fakeUser.id
}

let fakeCommunityId: string|Object

describe('Community Operations', () => {
  beforeAll(async () => {
    await database.query('CREATE TABLE users (id text primary key,name text not null,date timestamp default now());')
    await database.query('CREATE TABLE communities (id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, title VARCHAR(25) NOT NULL, description VARCHAR(100) NOT NULL, image TEXT, user_id TEXT, date TIMESTAMP DEFAULT now(), CONSTRAINT community_belongsTo_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE);')
    await database.query('CREATE TABLE userscommunities(id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, user_id TEXT, community_id INT, CONSTRAINT user_belongsToMany_communities FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE, CONSTRAINT community_belongsToMany_users FOREIGN KEY (community_id) REFERENCES communities (id) ON DELETE CASCADE )')
  })
  beforeEach(async () => {
    await database.query('INSERT INTO users(id, name) VALUES(${id}, ${name})', fakeUser)
    fakeCommunityId = await database.query('INSERT INTO communities(title, description, image, user_id) VALUES(${title}, ${description}, ${image}, ${user_id}) RETURNING id', fakeCommunity)
    fakeCommunityId = fakeCommunityId[0].id
  })
  afterAll(async () => {
    await database.query('DROP TABLE userscommunities;')
    await database.query('DROP TABLE communities;')
    await database.query('DROP TABLE users;')
    database.$pool.end()

    fs.readdir('public', (err, files) => {
      if (err) throw err

      for (const file of files) {
        fs.unlink(path.join('public', file), err => {
          if (err) throw err
        })
      }
    })
  })
  afterEach(async () => {
    await database.query('DELETE FROM communities;')
    await database.query('DELETE FROM users;')
  })

  it('Should get a community (by id)', async () => {
    await request(app)
      .get(`/communities/${fakeCommunityId}`)
      .expect(200)
      .then(response => {
        expect(response.body).toMatchObject(fakeCommunity)
      })
  })

  it('Should NOT get a community', async () => {
    await request(app)
      .get(`/communities/${fakeCommunityId}1`)
      .expect(404)
  })

  it('Should get communities from an User', async () => {
    await database.query('DELETE FROM userscommunities;')

    const foundComms = await database.query('SELECT * FROM communities WHERE title=${title}', { title: fakeCommunity.title })
    const comm = foundComms[0]
    await database.query('INSERT INTO userscommunities(user_id, community_id) VALUES(${user_id}, ${community_id})', { user_id: fakeUser.id, community_id: comm.id })

    await request(app)
      .get(`/communities/users/${fakeUser.id}`)
      .expect(200)
      .then(response => {
        expect(response.body).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              user_id: fakeUser.id,
              community_id: comm.id
            })
          ])
        )
      })
  })

  it('Should subscribe User to Community', async() => {
    await database.query('DELETE FROM userscommunities;')

    const foundComms = await database.query('SELECT * FROM communities WHERE title=${title}', { title: fakeCommunity.title })
    const comm = foundComms[0]

    await request(app)
      .post('/communities/subscribe')
      .send({ user_id: fakeUser.id, community_title: fakeCommunity.title })
      .expect(201)
      .then(response => {
        expect(response.body).toMatchObject({ user_id: fakeUser.id, community_id: comm.id })
      })
  })

  it('Should NOT subscribe User to Community', async() => {
    await database.query('DELETE FROM userscommunities;')

    await request(app)
      .post('/communities/subscribe')
      .send({ user_id: fakeUser.id, community_title: 'random title' })
      .expect(404)
  })

  it('Should Unsubscribe User from Community', async() => {
    await database.query('DELETE FROM userscommunities;')

    const foundComms = await database.query('SELECT * FROM communities WHERE title=${title}', { title: fakeCommunity.title })
    const comm = foundComms[0]

    await request(app)
      .post('/communities/subscribe')
      .send({ user_id: fakeUser.id, community_title: fakeCommunity.title })

    await request(app)
      .post('/communities/unsubscribe')
      .send({ user_id: fakeUser.id, community_title: fakeCommunity.title })
      .expect(204)

    await request(app)
      .get(`/communities/users/${fakeUser.id}`)
      .expect(200)
      .then(response => {
        expect(response.body).not.toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              community_id: comm.id
            })
          ])
        )
      })
  })

  it('Should create a Community', async () => {
    const newCommunity = {
      title: 'random title',
      description: 'random description',
      user_id: fakeUser.id
    }
    await request(app)
      .post('/communities')
      .field('community', JSON.stringify(newCommunity))
      .attach('community', './src/test/testUtils/test.png')
      .expect(201)
      .then(response => {
        fs.unlink(`public/${response.body.image}`, (err) => {
          if (err) {
            console.log(err)
          }
        })
        expect(response.body).toMatchObject(newCommunity)
      })
  })

  it('Should NOT create a Community - TITLE TOO LONG', async () => {
    const newCommunity = {
      title: `${fakeCommunity.title}.`,
      description: 'random description',
      user_id: fakeUser.id
    }
    await request(app)
      .post('/communities')
      .field('community', JSON.stringify(newCommunity))
      .attach('community', './src/test/testUtils/test.png')
      .expect(500)
  })

  it('Should NOT create a Community - CONTENT TOO LONG', async () => {
    const newCommunity = {
      title: 'random content',
      description: `${fakeCommunity.description}.`,
      user_id: fakeUser.id
    }
    await request(app)
      .post('/communities')
      .field('community', JSON.stringify(newCommunity))
      .attach('community', './src/test/testUtils/test.png')
      .expect(500)
  })

  it('Should NOT create a Community - EQUAL OBJECTS', async () => {
    await request(app)
      .post('/communities')
      .field('community', JSON.stringify(fakeCommunity))
      .attach('community', './src/test/testUtils/test.png')
      .expect(409)
  })

  it('Should update a Community - Change Title', async () => {
    const to_update = { ...fakeCommunity, title: 'changed title' }
    await request(app)
      .put(`/communities/${fakeCommunityId}`)
      .send(to_update)
      .expect(204)
  })

  it('Should update a Community - Change Description', async () => {
    const to_update = { ...fakeCommunity, description: 'changed description' }
    await request(app)
      .put(`/communities/${fakeCommunityId}`)
      .send(to_update)
      .expect(204)
  })

  it.only('Should update a Community - Change Image', async () => {
    const newCommunity = {
      title: 'random title',
      description: 'random description',
      user_id: fakeUser.id
    }
    await request(app)
      .post('/communities')
      .field('community', JSON.stringify(newCommunity))
      .attach('community', './src/test/testUtils/test.png')
      .then(async response => {
        console.log(response.body)
        await request(app)
          .put(`/communities/${response.body.id}/image`)
          .attach('community', './src/test/testUtils/test2.png')
          .expect(204)
      })
  })

  it('Should NOT update a Community - TITLE TOO LONG', async () => {
    const to_update = { ...fakeCommunity, title: `${fakeCommunity.title}.` }
    await request(app)
      .put(`/communities/${fakeCommunityId}`)
      .send(to_update)
      .expect(500)
  })

  it('Should NOT update a Community - DESCRIPTION TOO LONG', async () => {
    const to_update = { ...fakeCommunity, description: `${fakeCommunity.description}.` }
    await request(app)
      .put(`/communities/${fakeCommunityId}`)
      .send(to_update)
      .expect(500)
  })

  it('Should NOT update a Community - INVALID ID', async () => {
    const to_update = { ...fakeCommunity, description: 'random description' }
    await request(app)
      .put(`/communities/${fakeCommunityId}1`)
      .send(to_update)
      .expect(404)
  })

  it('Should delete a Community', async () => {
    await request(app)
      .delete(`/communities/${fakeCommunityId}`)
      .expect(204)

    await request(app)
      .get(`/communities/${fakeCommunityId}`)
      .expect(404)
  })
})
