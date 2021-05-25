import { UserModel } from '@models/UserModel'
import { IUserMethods } from 'src/interfaces/IUserMethods'
import database from '../infra/database'

export class UsersData implements IUserMethods {
  getUsers() {
    return database.query('SELECT * FROM users')
  }

  saveUser(user: UserModel) {
    return database.one('INSERT INTO users(id, name, email, password) VALUES(${id} ,${name}, ${email}, ${password}) RETURNING id, name, email',
      user)
  }

  getSingleUser(id: string) {
    return database.one('SELECT * FROM users WHERE id = ${id}',
      { id: id })
  }

  updateUser(user: UserModel, id: string) {
    return database.one('UPDATE users SET password=${password} WHERE id = ${id} RETURNING id, name, email',
      { ...user, id: id })
  }

  deleteUser(id: string) {
    return database.none('DELETE FROM users WHERE id = ${id}',
      { id: id })
  }
}
