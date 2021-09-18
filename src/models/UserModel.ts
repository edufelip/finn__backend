export class UserModel {
    // public readonly _id?: string
    public id: string
    public name: string
    public photo: string

    constructor() {
      Object.assign(this)
    }
}
