import { UserModel } from '@models/UserModel'

export interface IUserMethods {
    saveUser: (user: UserModel) => Promise<UserModel>

    getUserById: (id: string) => Promise<UserModel>

    updateUser: (id: string, name: string) => Promise<UserModel>

    deleteUser: (id: string) => Promise<null>
}
