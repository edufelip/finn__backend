import { CommentModel } from '@models/CommentModel'
import { CommunityModel } from '@models/CommunityModel'

export interface ICommunityMethods {
    getCommunitiesUser: (id: string) => Promise<CommentModel[]>

    getCommunityByTitle: (title: string) => Promise<CommunityModel>

    getCommunityById: (id: string) => Promise<CommunityModel>

    subscribeUserCommunity: (userId: string, commTitle: string) => Promise<CommunityModel>

    unsubscribeUserCommunity: (userId: string, commTitle: string) => Promise<CommunityModel>

    saveCommunity: (post: CommunityModel) => Promise<CommunityModel>

    updateCommunity: (title: string, community: CommunityModel) => Promise<CommunityModel>

    deleteCommunity: (id: string) => Promise<CommunityModel>
}
