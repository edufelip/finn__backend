export class PostModel {
    public readonly id?: string
    public content: string
    public photo: string
    public communityId: string
    public userId: string
    public likesCount: number

    constructor(props: Omit<PostModel, 'id'>) {
      Object.assign(this, props)
    }
}
