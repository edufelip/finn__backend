import { UserModel } from '@models/UserModel'
import { UsersData } from 'src/data/UsersData'
import { IUserMethods } from 'src/interfaces/IUserMethods'

export class UsersService implements IUserMethods {
  private readonly usersData: IUserMethods

  constructor(usersData: UsersData) {
    this.usersData = usersData
  }

  getUsers() {
    return this.usersData.getUsers()
  }

  saveUser(user: UserModel) {
    return this.usersData.saveUser(user)
  }

  async getSingleUser(id: string) {
    const user = await this.usersData.getSingleUser(id)
    if (!user) throw new Error('User not found')
    return user
  }

  async updateUser(id: string, name: string) {
    await this.getSingleUser(id)
    return this.usersData.updateUser(id, name)
  }

  deleteUser(id: string) {
    return this.usersData.deleteUser(id)
  }
}
