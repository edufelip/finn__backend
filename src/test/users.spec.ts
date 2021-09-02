import request from 'supertest'
import app from '../index'
import faker from 'faker'
import database from '../infra/database'
import { response } from 'express'

const fakeUser = {
  id: faker.datatype.uuid(),
  name: faker.internet.userName()
}

describe('User Operations', () => {
  beforeAll(async () => {
    await database.query('create table users (id text primary key,name text not null,date timestamp default now());')
  })
  beforeEach(async () => {
    await database.query('INSERT INTO users(id, name) VALUES(${id} ,${name}) RETURNING id, name', fakeUser)
  })
  afterAll(async () => {
    await database.query('drop table users;')
    database.$pool.end()
  })
  afterEach(async () => {
    await database.query('delete from users;')
  })

  it('Should get users', async () => {
    await request(app)
      .get('/users')
      .expect(200)
  })

  it('Should get a single user', async () => {
    await request(app)
      .get(`/users/${fakeUser.id}`)
      .expect(200)
      .then(response => {
        expect(response.body.id).toBe(fakeUser.id)
        expect(response.body.name).toBe(fakeUser.name)
      })
  })

  it('should create an user', async () => {
    const newUser = {
      id: faker.datatype.uuid(),
      name: faker.internet.userName()
    }

    await request(app)
      .post('/users/')
      .send(newUser)
      .expect('Content-Type', /json/)
      .expect(201)
      .then(response => {
        expect(response.body.id).toBe(newUser.id)
        expect(response.body.name).toBe(newUser.name)
      })
  })

  it('should update an user', async () => {
    // update it
    const newUser = {
      id: fakeUser.id,
      name: 'Random Name'
    }
    await request(app)
      .put(`/users/${fakeUser.id}`)
      .send(newUser)
      .expect(204)

    // then get it
    await request(app)
      .get(`/users/${fakeUser.id}`)
      .expect(200)
      .then(response => {
        expect(response.body.id).toBe(fakeUser.id)
        expect(response.body.name).toBe(newUser.name)
      })
  })

  it.only('should NOT update an user', async () => {
    const newUser = {
      id: faker.datatype.uuid(),
      name: 'Random Name'
    }
    await request(app)
      .put(`/users/${newUser.id}`)
      .send(newUser)
      .expect(404)
  })

  it('should delete an user', async () => {
    // delete
    await request(app)
      .delete(`/users/${fakeUser.id}`)
      .expect(204)

    // then search
    await request(app)
      .get('/users')
      .expect(200)
      .then(response => {
        expect(response.body).toEqual([])
      })
  })
})
