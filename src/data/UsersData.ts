import { UserModel } from '@models/UserModel'
import { IUserMethods } from 'src/interfaces/IUserMethods'

export class UsersData implements IUserMethods {
  public db
  constructor(database) {
    this.db = database
  }

  getUsers() {
    return this.db.query('SELECT * FROM users')
  }

  saveUser(user: UserModel) {
    return this.db.one('INSERT INTO users(id, name) VALUES(${id} ,${name}) RETURNING id, name', user)
  }

  getSingleUser(id: string) {
    return this.db.one('SELECT * FROM users WHERE id = ${id}',
      { id: id })
  }

  updateUser(id: string, name: string) {
    return this.db.one('UPDATE users SET name=${name} WHERE id = ${id} RETURNING id, name', { name: name, id: id })
  }

  deleteUser(id: string) {
    return this.db.none('DELETE FROM users WHERE id = ${id}',
      { id: id })
  }
}
