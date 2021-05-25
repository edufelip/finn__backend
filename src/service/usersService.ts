import { UserModel } from '@models/UserModel'
import { UsersData } from 'src/data/UsersData'
import { IUserMethods } from 'src/interfaces/IUserMethods'

class UsersService implements IUserMethods {
  private readonly usersData: UsersData

  constructor(usersData: UsersData) {
    this.usersData = usersData
  }

  getUsers() {
    return this.usersData.getUsers()
  }

  saveUser(user: UserModel) {
    return this.usersData.saveUser(user)
  }

  getSingleUser(id: string) {
    return this.usersData.getSingleUser(id)
  }

  updateUser(user: UserModel, id: string) {
    return this.usersData.updateUser(user, id)
  }

  deleteUser(id: string) {
    return this.usersData.deleteUser(id)
  }
}

const usersData = new UsersData()
const usersService = new UsersService(usersData)

export { usersService }
