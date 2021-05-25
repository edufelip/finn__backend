import { UserModel } from '@models/UserModel'

export interface IUserMethods {
    getUsers: () => Promise<UserModel[]>

    saveUser: (user: UserModel) => Promise<UserModel>

    getSingleUser: (id: string) => Promise<UserModel>

    updateUser: (user: UserModel, id: string) => Promise<UserModel>

    deleteUser: (id: string) => Promise<null>
}
