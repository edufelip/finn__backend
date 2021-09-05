export class LikeModel {
    public readonly id?: string
    public user_id: string
    public post_id: string

    constructor(props: Omit<LikeModel, 'id'>) {
      Object.assign(this, props)
    }
}
