import communityData from '@data/CommunityData'
import { ICommunityMethods } from '@interfaces/ICommunityMethods'
import { CommunityModel } from '@models/CommunityModel'

class CommunityService implements ICommunityMethods {
  private readonly communityData: ICommunityMethods

  constructor(communityData: ICommunityMethods) {
    this.communityData = communityData
  }

  async getCommunities(query: string) {
    const communities = await communityData.getCommunities(query)
    return communities
  }

  async getCommunityByTitle(title: string) {
    const community = await communityData.getCommunityByTitle(title)
    if (!community) throw new Error('Community not found')
    return community
  }

  async getCommunityById(id: string) {
    const community = await communityData.getCommunityById(id)
    if (!community) throw new Error('Community not found')
    return community
  }

  async getCommunitiesUser(id: string) {
    const communities = await communityData.getCommunitiesUser(id)
    return communities
  }

  async subscribeUserCommunity(user_id: string, comm_id: string) {
    const existing_community = await communityData.getCommunityById(comm_id)
    if (!existing_community) throw new Error('Community not found')
    return this.communityData.subscribeUserCommunity(user_id, comm_id)
  }

  async unsubscribeUserCommunity(user_id: string, comm_id: string) {
    const existing_community = await communityData.getCommunityById(comm_id)
    if (!existing_community) throw new Error('Community not found')
    return this.communityData.unsubscribeUserCommunity(user_id, comm_id)
  }

  async getSubscription(userId: string, commId: string) {
    const existing_subscription = await communityData.getSubscription(userId, commId)
    if (!existing_subscription) throw new Error('Subscription not found')
    return existing_subscription
  }

  async getCommunitySubscribersCount(comm_id: string) {
    const existing_community = await communityData.getCommunityById(comm_id)
    if (!existing_community) throw new Error('Community not found')
    return this.communityData.getCommunitySubscribersCount(comm_id)
  }

  async saveCommunity(community: CommunityModel) {
    const existing_community = await communityData.getCommunityByTitle(community.title)
    if (existing_community) throw new Error('Community already exists')
    return this.communityData.saveCommunity(community)
  }

  async updateCommunity(id: string, community: CommunityModel) {
    const found_community = await communityData.getCommunityById(id)
    if (!found_community) throw new Error('Community not found')
    return this.communityData.updateCommunity(id, community)
  }

  async updateCommunityImage(id: string, image: string) {
    return this.communityData.updateCommunityImage(id, image)
  }

  async deleteCommunity(id: string) {
    return this.communityData.deleteCommunity(id)
  }
}

export default new CommunityService(communityData)
