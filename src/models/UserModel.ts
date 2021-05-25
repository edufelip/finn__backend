export class UserModel {
    public readonly _id?: string

    public name: string

    public email: string

    public password: string

    constructor(props: Omit<UserModel, '_id'>) {
      Object.assign(this, props)
    }
}
