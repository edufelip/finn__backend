import { IPostMethods } from '@interfaces/IPostMethods'
import { PostModel } from '@models/PostModel'
import { IDatabase } from 'pg-promise'
import database from '../infra/database'

class PostData implements IPostMethods {
  public db: IDatabase<null>
  constructor(database: IDatabase<null>) {
    this.db = database
  }

  getPostsFeed(id: string, page: string) {
    const jump = (parseInt(page) - 1) * 10
    return this.db.query('SELECT po.id, po.content, po.image, po.user_id, po.date, po.community_id FROM posts po JOIN userscommunities uc ON uc.community_id = po.community_id WHERE uc.user_id = ${id} ORDER BY po.date DESC LIMIT 10 OFFSET ${jump};', { id: id, jump: jump })
  }

  getPostsFromUser(id: string, page: string) {
    const jump = (parseInt(page) - 1) * 10
    return this.db.query('SELECT * FROM posts WHERE user_id=${user_id} ORDER BY posts.date DESC LIMIT 10 OFFSET ${jump}',
      { user_id: id, jump: jump })
  }

  getPostsFromCommunity(id: string, page: string) {
    const jump = (parseInt(page) - 1) * 10
    return this.db.query('SELECT * FROM posts WHERE community_id=${community_id} ORDER BY posts.date DESC LIMIT 10 OFFSET ${jump}',
      { community_id: id, jump: jump })
  }

  getSinglePost(id: string) {
    return this.db.oneOrNone('SELECT * FROM posts WHERE id = ${id}',
      { id: id })
  }

  savePost(post: PostModel) {
    return this.db.one('INSERT INTO posts(content, image, user_id, community_id) VALUES(${content}, ${image}, ${user_id}, ${community_id}) RETURNING *',
      post)
  }

  updatePost(id: string, post: PostModel) {
    return this.db.none('UPDATE posts SET content=${content}, image=${image} WHERE id = ${id};',
      { ...post, id: id })
  }

  deletePost(id: string) {
    return this.db.none('DELETE FROM posts WHERE id=${id}', { id: id })
  }

  findLike(user_id: string, post_id: string) {
    return this.db.oneOrNone('SELECT * FROM likes WHERE user_id=${user_id} AND post_id=${post_id}', { user_id: user_id, post_id: post_id })
  }

  giveLikeToPost(user_id: string, post_id: string) {
    return this.db.one('INSERT INTO likes(user_id, post_id) VALUES(${user_id}, ${post_id}) RETURNING *', { user_id: user_id, post_id: post_id })
  }

  removeLikeFromPost(user_id: string, post_id: string) {
    return this.db.none('DELETE FROM likes WHERE user_id=${user_id} AND post_id=${post_id}', { user_id: user_id, post_id: post_id })
  }

  getLikeCount(post_id: string) {
    return this.db.oneOrNone('SELECT COUNT(*) FROM likes WHERE post_id=${post_id}', { post_id: post_id })
  }
}

export default new PostData(database)
