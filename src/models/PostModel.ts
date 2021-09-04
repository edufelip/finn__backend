export class PostModel {
    public readonly id?: string
    public content: string
    public photo: string
    public community_id: string
    public user_id: string

    constructor(props: Omit<PostModel, 'id'>) {
      Object.assign(this, props)
    }
}
