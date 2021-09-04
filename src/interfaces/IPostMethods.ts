import { PostModel } from '@models/PostModel'

export interface IPostMethods {
    getPostsFeed: (id: string, page: string) => Promise<PostModel[]>

    getPostsFromUser: (id: string) => Promise<PostModel[]>

    getPostsFromCommunity: (id: string) => Promise<PostModel[]>

    getSinglePost: (id: string) => Promise<PostModel>

    savePost: (post: PostModel) => Promise<PostModel>

    updatePost: (id: string, post: PostModel) => Promise<PostModel>

    deletePost: (id: string) => Promise<PostModel>

    increaseLikePost: (user_id: string, post_id: string) => Promise<PostModel>

    decreaseLikePost: (user_id: string, post_id: string) => Promise<PostModel>

    getLikeCount: (post_id: string) => Promise<number>
}
