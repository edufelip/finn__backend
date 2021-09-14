import express, { NextFunction, Request, Response } from 'express'
import communityService from '@service/communityService'
import { upload } from '../config/multer'
import fs from 'fs'
import { CommunityModel } from '@models/CommunityModel'

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

router.get('/:id/subscribers', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    const response = await communityService.getCommunitySubscribersCount(id)
    const likes = parseInt(response.count)
    res.json(likes)
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
  upload.single('community')(req, res, async (err) => {
    if (err) {
      res.status(500).send(err.message)
    } else {
      const parse = JSON.parse(req.body.community)
      const community = { ...parse, image: req.file.filename }
      try {
        const new_community = await communityService.saveCommunity(community)
        res.status(201).json(new_community)
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
  const community_to_update = req.body
  try {
    await communityService.updateCommunity(id, community_to_update)
    res.status(204).end()
  } catch (e) {
    next(e)
  }
})

router.put('/:id/image', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  upload.single('community')(req, res, async (err) => {
    if (err) {
      res.status(500).send(err.message)
    } else {
      try {
        const foundCommunity: CommunityModel = await communityService.getCommunityById(id)
        await communityService.updateCommunityImage(foundCommunity.id, req.file.filename)
        fs.unlink(`public/${foundCommunity.image}`, (err) => {
          if (err) {
            console.log(err)
          }
        })
        res.status(204).end()
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

router.delete('/:id', async function(req: Request, res: Response, next: NextFunction) {
  const id = req.params.id
  try {
    const community = await communityService.getCommunityById(id)
    await communityService.deleteCommunity(id)
    fs.unlink(`public/${community.image}`, (err) => {
      if (err) {
        console.log(err)
      }
    })
    return res.status(204).json('Successfully deleted')
  } catch (e) {
    next(e)
  }
})

export default router
