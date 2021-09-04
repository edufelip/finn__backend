export class CommunityModel {
    public readonly id?: string
    public title: string
    public description: string
    public image: string
    public user_id: string

    constructor(props: Omit<CommunityModel, 'id'>) {
      Object.assign(this, props)
    }
}
