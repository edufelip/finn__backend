import express, { NextFunction, Request, Response } from 'express'
import communityService from '@service/communityService'
const router = express.Router()

router.get('/users/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    const foundCommunities = await communityService.getCommunitiesUser(id)
    res.status(200).json(foundCommunities)
  } catch (e) {
    next(e)
  }
})

router.get('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    const foundCommunity = await communityService.getCommunityById(id)
    res.status(200).json(foundCommunity)
  } catch (e) {
    next(e)
  }
})

router.post('/subscribe', async function(req: Request, res: Response, next: NextFunction) {
  const user_id = req.body.user_id
  const comm_title = req.body.community_title
  try {
    const new_subscription = await communityService.subscribeUserCommunity(user_id, comm_title)
    res.status(201).json(new_subscription)
  } catch (e) {
    next(e)
  }
})

router.post('/unsubscribe', async function(req: Request, res: Response, next: NextFunction) {
  const user_id = req.body.user_id
  const comm_title = req.body.community_title
  try {
    await communityService.unsubscribeUserCommunity(user_id, comm_title)
    res.status(204).json('Successfully deleted')
  } catch (e) {
    next(e)
  }
})

router.post('/', async function(req: Request, res: Response, next: NextFunction) {
  const community = req.body
  try {
    const new_community = await communityService.saveCommunity(community)
    res.status(201).json(new_community)
  } catch (e) {
    next(e)
  }
})

router.put('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  const community_to_update = req.body
  try {
    await communityService.updateCommunity(id, community_to_update)
    res.status(204).end()
  } catch (e) {
    next(e)
  }
})

router.delete('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    await communityService.deleteCommunity(id)
    return res.status(204).json('Successfully deleted')
  } catch (e) {
    next(e)
  }
})

export default router
