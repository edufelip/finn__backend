import { PostModel } from '@models/PostModel'

export interface IPostMethods {
    getPostsFeed: (id: string) => Promise<PostModel[]>

    getPostsFromUser: (id: string) => Promise<PostModel[]>

    getPostsFromCommunity: (id: string) => Promise<PostModel[]>

    getSinglePost: (id: string) => Promise<PostModel>

    savePost: (post: PostModel) => Promise<PostModel>

    updatePost: (id: string, post: PostModel) => Promise<PostModel>

    deletePost: (id: string) => Promise<PostModel>
}
