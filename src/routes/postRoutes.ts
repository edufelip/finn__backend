import postService from '@service/postService'
import express, { NextFunction, Request, Response } from 'express'
const router = express.Router()

router.get('/users/:id/feed', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    const foundPosts = await postService.getPostsFeed(id)
    res.status(201).json(foundPosts)
  } catch (e) {
    next(e)
  }
})

router.get('/users/:id', async function(req: Request, res: Response, next: NextFunction) {
  const userId = req.params.id
  // checar usuario na table usercommunities
  try {
    const foundPosts = await postService.getPostsFromUser(userId)
    res.status(200).json(foundPosts)
  } catch (e) {
    next(e)
  }
})

router.get('/communities/:id', async function(req: Request, res: Response, next: NextFunction) {
  const communityId = req.params.id
  // checar community na table usercommunities
  try {
    const foundPosts = await postService.getPostsFromCommunity(communityId)
    res.status(200).json(foundPosts)
  } catch (e) {
    next(e)
  }
})

router.get('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    const foundPost = await postService.getSinglePost(id)
    res.status(200).json(foundPost)
  } catch (e) {
    next(e)
  }
})

router.post('/', async function(req: Request, res: Response, next: NextFunction) {
  const post = req.body
  try {
    const newPost = await postService.savePost(post)
    res.status(201).json(newPost)
  } catch (e) {
    next(e)
  }
})

router.put('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  const postToUpdate = req.body
  try {
    await postService.updatePost(id, postToUpdate)
    res.status(204).end()
  } catch (e) {
    next(e)
  }
})

router.delete('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    await postService.deletePost(id)
    return res.status(204).json('Successfully deleted')
  } catch (e) {
    next(e)
  }
})

export default router
