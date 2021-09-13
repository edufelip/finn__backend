import { PostModel } from '@models/PostModel'
import { LikeModel } from '@models/LikeModel'

export interface LikeResponse {
    count: string
}

export interface IPostMethods {
    getPostsFeed: (id: string, page: string) => Promise<PostModel[]>

    getPostsFromUser: (id: string, page: string) => Promise<PostModel[]>

    getPostsFromCommunity: (id: string, page: string) => Promise<PostModel[]>

    getSinglePost: (id: string) => Promise<PostModel>

    savePost: (post: PostModel) => Promise<PostModel>

    updatePost: (id: string, post: PostModel) => Promise<PostModel>

    deletePost: (id: string) => Promise<PostModel>

    findLike: (user_id: string, post_id: string) => Promise<LikeModel>

    giveLikeToPost: (user_id: string, post_id: string) => Promise<LikeModel>

    removeLikeFromPost: (user_id: string, post_id: string) => Promise<LikeModel>

    getLikeCount: (post_id: string) => Promise<LikeResponse>
}
