import commentData from '@data/CommentData'
import { ICommentMethods } from '@interfaces/ICommentMethods'
import { CommentModel } from '@models/CommentModel'

class CommentService implements ICommentMethods {
  private readonly commentData: ICommentMethods

  constructor(commentData: ICommentMethods) {
    this.commentData = commentData
  }

  async getSingleComment(id: string) {
    const comment = await commentData.getSingleComment(id)
    if (!comment) throw new Error('Comment not found')
    return comment
  }

  async getCommentsUser(id: string) {
    const comments = await commentData.getCommentsUser(id)
    return comments
  }

  async getCommentsPost(id: string) {
    const comments = await commentData.getCommentsPost(id)
    return comments
  }

  async saveComment(comment: CommentModel) {
    return this.commentData.saveComment(comment)
  }

  async updateComment(id: string, comment: CommentModel) {
    const found_comment = await commentData.getSingleComment(id)
    if (!found_comment) throw new Error('Comment not found')
    return this.commentData.updateComment(id, comment)
  }

  async deleteComment(id: string) {
    return this.commentData.deleteComment(id)
  }
}

export default new CommentService(commentData)
