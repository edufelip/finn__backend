import { UserModel } from '@models/UserModel'
import userService from '@service/userService'
import { Request, Response, Router } from 'express'

const router = Router()

router.get('/:id', async function(req: Request, res: Response, next) {
  const id = req.params.id
  try {
    const found_user = await userService.getUserById(id)
    res.json(found_user)
  } catch (e) {
    next(e)
  }
})

router.post('/', async function(req: Request, res: Response, next) {
  const user = req.body
  try {
    const new_user = await userService.saveUser(user)
    res.status(201).json(new_user)
  } catch (e) {
    next(e)
  }
})

router.put('/:id', async function(req: Request, res: Response, next) {
  const id = req.params.id
  const user_to_update = req.body
  try {
    await userService.updateUser(id, user_to_update.name)
    res.status(204).end()
  } catch (e) {
    next(e)
  }
})

router.delete('/:id', async function(req: Request, res: Response, next) {
  const id = req.params.id
  try {
    await userService.deleteUser(id)
    res.status(204).end('Successfully deleted')
  } catch (e) {
    next(e)
  }
})

export default router
