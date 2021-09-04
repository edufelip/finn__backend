import { IDatabase } from 'pg-promise'
import database from '../infra/database'
import { ICommunityMethods } from '@interfaces/ICommunityMethods'
import { CommunityModel } from '@models/CommunityModel'

class CommunityData implements ICommunityMethods {
  public db: IDatabase<null>
  constructor(database: IDatabase<null>) {
    this.db = database
  }

  getCommunityByTitle(title: string) {
    return this.db.oneOrNone('SELECT * FROM communities WHERE title = ${title}', { title: title })
  }

  getCommunityById(id: string) {
    return this.db.oneOrNone('SELECT * FROM communities WHERE id = ${id}', { id: id })
  }

  getCommunitiesUser(id: string) {
    return this.db.query('SELECT * FROM communities co JOIN userscommunities uc ON uc.user_id = ${id} AND co.id = uc.community_id', { id: id })
  }

  subscribeUserCommunity(user_id: string, comm_id: string) {
    return this.db.one('INSERT INTO userscommunities(user_id, community_id) VALUES(${user_id}, ${community_id}) RETURNING *', { user_id: user_id, community_id: comm_id })
  }

  unsubscribeUserCommunity(user_id: string, comm_id: string) {
    return this.db.none('DELETE FROM userscommunities WHERE user_id=${user_id} AND community_id=${community_id}', { user_id: user_id, community_id: comm_id })
  }

  saveCommunity(community: CommunityModel) {
    return this.db.one('INSERT INTO communities(title, description, image, user_id) VALUES(${title}, ${description}, ${image}, ${user_id}) RETURNING *',
      community)
  }

  updateCommunity(id: string, community: CommunityModel) {
    return this.db.none('UPDATE communities SET title=${title}, description=${description}, image=${image} WHERE id = ${id}', { ...community, id: id })
  }

  deleteCommunity(id: string) {
    return this.db.none('DELETE FROM communities WHERE id = ${id}', { id: id })
  }
}

export default new CommunityData(database)
