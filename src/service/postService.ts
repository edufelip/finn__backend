import { IPostMethods } from '@interfaces/IPostMethods'
import postData from '@data/PostData'
import { PostModel } from '@models/PostModel'

class PostService implements IPostMethods {
  private readonly postData: IPostMethods

  constructor(postData: IPostMethods) {
    this.postData = postData
  }

  async getPostsFeed(id: string, page: string) {
    const posts = await postData.getPostsFeed(id, page)
    return posts
  }

  async getPostsFromUser(id: string) {
    const posts = await postData.getPostsFromUser(id)
    return posts
  }

  async getPostsFromCommunity(id: string) {
    const posts = await postData.getPostsFromCommunity(id)
    return posts
  }

  async getSinglePost(id: string) {
    const post = await postData.getSinglePost(id)
    if (!post) throw new Error('Post not found')
    return post
  }

  async savePost(post: PostModel) {
    return this.postData.savePost(post)
  }

  async updatePost(id: string, post: PostModel) {
    const found_post = await postData.getSinglePost(id)
    if (!found_post) throw new Error('Post not found')
    return this.postData.updatePost(id, post)
  }

  async deletePost(id: string) {
    return this.postData.deletePost(id)
  }

  async getLikeCount(post_id: string) {
    return this.postData.getLikeCount(post_id)
  }

  async increaseLikePost(user_id: string, post_id: string) {
    return this.postData.increaseLikePost(user_id, post_id)
  }

  async decreaseLikePost(user_id: string, post_id: string) {
    return this.postData.decreaseLikePost(user_id, post_id)
  }
}

export default new PostService(postData)
