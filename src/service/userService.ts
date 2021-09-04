import { UserModel } from '@models/UserModel'
import userData from '../data/UserData'
import { IUserMethods } from 'src/interfaces/IUserMethods'

class UserService implements IUserMethods {
  private readonly userData: IUserMethods

  constructor(userData: IUserMethods) {
    this.userData = userData
  }

  getUsers() {
    return this.userData.getUsers()
  }

  async saveUser(user: UserModel) {
    const found_user = await this.userData.getSingleUser(user.id)
    if (found_user) {
      throw new Error('User already exists')
    }
    return this.userData.saveUser(user)
  }

  async getSingleUser(id: string) {
    const user = await this.userData.getSingleUser(id)
    if (!user) {
      throw new Error('User not found')
    }
    return user
  }

  async updateUser(id: string, name: string) {
    await this.getSingleUser(id)
    return this.userData.updateUser(id, name)
  }

  deleteUser(id: string) {
    return this.userData.deleteUser(id)
  }
}

export default new UserService(userData)
