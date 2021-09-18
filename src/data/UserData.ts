import { UserModel } from '@models/UserModel'
import { IDatabase } from 'pg-promise'
import { IUserMethods } from 'src/interfaces/IUserMethods'
import database from '../infra/database'

class UserData implements IUserMethods {
  public db: IDatabase<null>
  constructor(database: IDatabase<null>) {
    this.db = database
  }

  saveUser(user: UserModel) {
    return this.db.one('INSERT INTO users(id, name, photo) VALUES(${id} ,${name}, ${photo}) RETURNING *', user)
  }

  getUserById(id: string) {
    return this.db.oneOrNone('SELECT * FROM users WHERE id = ${id}', { id: id })
  }

  updateUser(id: string, name: string) {
    return this.db.none('UPDATE users SET name=${name} WHERE id = ${id}', { name: name, id: id })
  }

  deleteUser(id: string) {
    return this.db.none('DELETE FROM users WHERE id = ${id}', { id: id })
  }
}

export default new UserData(database)
