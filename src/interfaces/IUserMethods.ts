import { UserModel } from '@models/UserModel'

export interface IUserMethods {
    getUsers: () => Promise<UserModel[]>

    saveUser: (user: UserModel) => Promise<UserModel>

    getSingleUser: (id: string) => Promise<UserModel>

    updateUser: (id: string, name: string) => Promise<UserModel>

    deleteUser: (id: string) => Promise<null>
}
