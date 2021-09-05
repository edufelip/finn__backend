import { CommentModel } from '@models/CommentModel'

export interface ICommentMethods {
    getCommentsPost: (id: string) => Promise<CommentModel[]>

    getCommentsUser: (id: string) => Promise<CommentModel[]>

    getSingleComment: (id: string) => Promise<CommentModel>

    saveComment: (comment: CommentModel) => Promise<CommentModel>

    updateComment: (id: string, comment: CommentModel) => Promise<CommentModel>

    deleteComment: (id: string) => Promise<CommentModel>
}
