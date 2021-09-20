import postService from '@service/postService'
import express, { NextFunction, request, Request, Response } from 'express'
import { upload } from '../config/multer'
import fs from 'fs'
const router = express.Router()

router.get('/users/:id/feed', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  const page = req.query && req.query.page && parseInt(req.query.page as any) > 0 ? (req.query as any).page : '1'
  try {
    const foundPosts = await postService.getPostsFeed(id, page)
    res.status(200).json(foundPosts)
  } catch (e) {
    next(e)
  }
})

router.get('/users/:id', async function(req: Request, res: Response, next: NextFunction) {
  const user_id = req.params.id
  const page = req.query && req.query.page && parseInt(req.query.page as any) > 0 ? (req.query as any).page : '1'
  try {
    const found_posts = await postService.getPostsFromUser(user_id, page)
    res.status(200).json(found_posts)
  } catch (e) {
    next(e)
  }
})

router.get('/:postId/users/:userId', async function(req: Request, res: Response, next: NextFunction) {
  const post_id = req.params.postId
  const user_id = req.params.userId
  try {
    const found_like = await postService.findLike(user_id, post_id)
    const resp = found_like ? 1 : 0
    res.status(200).json(resp)
  } catch (e) {
    next(e)
  }
})

router.get('/communities/:id', async function(req: Request, res: Response, next: NextFunction) {
  const community_id = req.params.id
  const page = req.query && req.query.page && parseInt(req.query.page as any) > 0 ? (req.query as any).page : '1'
  try {
    const found_posts = await postService.getPostsFromCommunity(community_id, page)
    res.status(200).json(found_posts)
  } catch (e) {
    next(e)
  }
})

router.get('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    const found_post = await postService.getSinglePost(id)
    res.status(200).json(found_post)
  } catch (e) {
    next(e)
  }
})

router.post('/', async function(req: Request, res: Response, next: NextFunction) {
  upload.single('post')(req, res, async (err) => {
    if (err) {
      res.status(500).send(err.message)
    } else {
      const parse = JSON.parse(req.body.post)
      const post = { ...parse, image: req.file !== undefined ? req.file.filename : null }
      try {
        const new_post = await postService.savePost(post)
        res.status(201).json(new_post)
      } catch (e) {
        fs.unlink(`public/${req.file.filename}`, (err) => {
          if (err) {
            console.log(err)
          }
        })
        next(e)
      }
    }
  })
})

router.put('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  const post_to_update = req.body
  try {
    await postService.updatePost(id, post_to_update)
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

router.get('/:id/likes', async function(req: Request, res: Response, next: NextFunction) {
  const post_id = req.params.id
  try {
    const response = await postService.getLikeCount(post_id)
    const likes = parseInt(response.count)
    res.json(likes)
  } catch (e) {
    next(e)
  }
})

router.post('/likes', async function(req: Request, res: Response, next: NextFunction) {
  const { user_id, post_id } = req.body
  try {
    const newLike = await postService.giveLikeToPost(user_id, post_id)
    res.status(201).json(newLike)
  } catch (e) {
    next(e)
  }
})

router.post('/likes/:id', async function(req: Request, res: Response, next: NextFunction) {
  const post_id = req.params.id
  const user_id = req.body.id
  try {
    await postService.removeLikeFromPost(user_id, post_id)
    return res.status(204).json('Successfully deleted')
  } catch (e) {
    next(e)
  }
})

export default router
