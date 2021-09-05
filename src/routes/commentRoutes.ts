import express, { NextFunction, Request, Response } from 'express'
import commentService from '@service/commentService'

const router = express.Router()

router.get('/posts/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    const foundComments = await commentService.getCommentsPost(id)
    res.status(200).json(foundComments)
  } catch (e) {
    next(e)
  }
})

router.get('/users/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    const foundComments = await commentService.getCommentsUser(id)
    res.status(200).json(foundComments)
  } catch (e) {
    next(e)
  }
})

router.get('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    const foundComment = await commentService.getSingleComment(id)
    res.status(200).json(foundComment)
  } catch (e) {
    next(e)
  }
})

router.post('/', async function(req: Request, res: Response, next: NextFunction) {
  const comment = req.body
  try {
    const newComment = await commentService.saveComment(comment)
    res.status(201).json(newComment)
  } catch (e) {
    next(e)
  }
})

router.put('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  const commentToUpdate = req.body
  try {
    await commentService.updateComment(id, commentToUpdate)
    res.status(204).end()
  } catch (e) {
    next(e)
  }
})

router.delete('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    await commentService.deleteComment(id)
    return res.status(204).json('Successfully deleted')
  } catch (e) {
    next(e)
  }
})

export default router
