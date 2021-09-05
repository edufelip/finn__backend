export class CommentModel {
    public readonly id?: string
    public content: string
    public user_id: string
    public post_id: string

    constructor(props: Omit<CommentModel, 'id'>) {
      Object.assign(this, props)
    }
}
