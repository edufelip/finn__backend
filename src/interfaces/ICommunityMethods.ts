import { CommentModel } from '@models/CommentModel'
import { CommunityModel } from '@models/CommunityModel'

export interface SubscribersResponse {
    count: string
}

export interface Subscription {
    id: string,
    user_id: string,
    community_id: string
}

export interface ICommunityMethods {
    getCommunitiesUser: (id: string) => Promise<CommentModel[]>

    getCommunityByTitle: (title: string) => Promise<CommunityModel>

    getCommunityById: (id: string) => Promise<CommunityModel>

    subscribeUserCommunity: (userId: string, commTitle: string) => Promise<CommunityModel>

    unsubscribeUserCommunity: (userId: string, commTitle: string) => Promise<CommunityModel>

    getSubscription: (userId: string, commId: string) => Promise<Subscription>

    getCommunitySubscribersCount: (comm_id: string) => Promise<SubscribersResponse>

    saveCommunity: (post: CommunityModel) => Promise<CommunityModel>

    updateCommunity: (title: string, community: CommunityModel) => Promise<CommunityModel>

    updateCommunityImage: (id: string, image: string) => Promise<CommunityModel>

    deleteCommunity: (id: string) => Promise<CommunityModel>
}
