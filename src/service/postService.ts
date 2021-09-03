import { IPostMethods } from '@interfaces/IPostMethods'
import postData from '@data/PostData'
import { PostModel } from '@models/PostModel'

class PostService implements IPostMethods {
  private readonly postData: IPostMethods

  constructor(postData: IPostMethods) {
    this.postData = postData
  }

  async getPostsFeed(id: string) {
    const posts = await postData.getPostsFeed(id)
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
    const existingPost = await postData.getSinglePost(post.id)
    if (existingPost) throw new Error('Post already exists')
    return this.postData.savePost(post)
  }

  async updatePost(id: string, post: PostModel) {
    const foundPost = await postData.getSinglePost(id)
    if (!foundPost) throw new Error('Post not found')
    return this.postData.updatePost(id, post)
  }

  async deletePost(id: string) {
    return this.postData.deletePost(id)
  }
}

export default new PostService(postData)
