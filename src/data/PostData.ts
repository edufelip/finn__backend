import { IPostMethods } from '@interfaces/IPostMethods'
import { PostModel } from '@models/PostModel'
import { IDatabase } from 'pg-promise'
import database from '../infra/database'

class PostData implements IPostMethods {
  public db: IDatabase<null>
  constructor(database: IDatabase<null>) {
    this.db = database
  }

  getPostsFeed(id: string) {
    return this.db.many('SELECT * from posts po JOIN userscommunities uc ON uc.user_id = ${id} and uc.community_id = po.community_id;', id)
  }

  getPostsFromUser(id: string) {
    return this.db.many('SELECT p.id, p.content, p.image, p.likes_count FROM posts p JOIN users u ON p.user_id = ${id} ORDER BY p.date', id)
  }

  getPostsFromCommunity(id: string) {
    return this.db.many('SELECT p.id, p.content, p.image, p.likes_count FROM posts p JOIN communities c ON p.community_id = ${id} ORDER BY p.date', id)
  }

  getSinglePost(id: string) {
    return this.db.one('SELECT * FROM posts WHERE id = ${id}',
      { id: id })
  }

  savePost(post: PostModel) {
    return this.db.one('INSERT INTO posts(content, image, likes_count, user_id, community_id) VALUES(${content}, ${image}, ${likes_count}, ${user_id}, ${community_id}) RETURNING id, name',
      post)
  }

  updatePost(id: string, post: PostModel) {
    return this.db.one('UPDATE users SET content=${content}, image=${image}, likes_count=${likes_count} WHERE id = ${id} RETURNING', { post: post, id: id })
  }

  deletePost(id: string) {
    return this.db.none('DELETE FROM posts WHERE id=${id}', id)
  }
}

export default new PostData(database)
