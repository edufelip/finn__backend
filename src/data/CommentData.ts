import { ICommentMethods } from '@interfaces/ICommentMethods'
import { CommentModel } from '@models/CommentModel'
import { IDatabase } from 'pg-promise'
import database from '../infra/database'

class CommentData implements ICommentMethods {
    public db: IDatabase<null>
    constructor(database: IDatabase<null>) {
      this.db = database
    }

    getCommentsUser(id: string) {
      return this.db.query('SELECT * FROM comments WHERE user_id = ${id} ORDER BY date DESC LIMIT 20', { id: id })
    }

    getCommentsPost(id: string) {
      return this.db.query('SELECT * FROM comments WHERE post_id = ${id} ORDER BY date DESC LIMIT 20', { id: id })
    }

    getSingleComment(id: string) {
      return this.db.oneOrNone('SELECT * FROM comments WHERE id = ${id}', { id: id })
    }

    saveComment(comment: CommentModel) {
      return this.db.one('INSERT INTO comments(content, user_id, post_id) VALUES(${content}, ${user_id}, ${post_id}) RETURNING *', comment)
    }

    updateComment(id: string, comment: CommentModel) {
      return this.db.none('UPDATE comments SET content=${content} WHERE id = ${id}', { id: id, content: comment.content })
    }

    deleteComment(id: string) {
      return this.db.none('DELETE FROM comments WHERE id = ${id}', { id: id })
    }
}

export default new CommentData(database)
