import { UserModel } from '@models/UserModel'
import userData from '../data/UserData'
import { IUserMethods } from 'src/interfaces/IUserMethods'

class UserService implements IUserMethods {
  private readonly userData: IUserMethods

  constructor(userData: IUserMethods) {
    this.userData = userData
  }

  async saveUser(user: UserModel) {
    const found_user = await this.userData.getUserById(user.id)
    if (found_user) {
      throw new Error('User already exists')
    }
    return this.userData.saveUser(user)
  }

  async getUserById(id: string) {
    const user = await this.userData.getUserById(id)
    if (!user) {
      throw new Error('User not found')
    }
    return user
  }

  async updateUser(id: string, name: string) {
    await this.getUserById(id)
    return this.userData.updateUser(id, name)
  }

  deleteUser(id: string) {
    return this.userData.deleteUser(id)
  }
}

export default new UserService(userData)
