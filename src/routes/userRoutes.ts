import { Router } from 'express'
import { UsersService } from '../service/usersService'
import { UsersData } from '../data/UsersData'
import database from '../infra/database'

const usersData = new UsersData(database)
const usersService = new UsersService(usersData)
const router = Router()

router.get('/', async function(req, res) {
  const users = await usersService.getUsers()
  res.json(users)
})

router.get('/:id', async function(req, res) {
  const id = req.params.id
  const foundUser = await usersService.getSingleUser(id)
  res.json(foundUser)
})

router.post('/', async function(req, res) {
  const user = req.body
  const newUser = await usersService.saveUser(user)
  res.status(201).json(newUser)
})

router.put('/:id', async function(req, res) {
  const id = req.params.id
  const userToUpdate = req.body
  try {
    await usersService.updateUser(id, userToUpdate.name)
    res.status(204).end()
  } catch (e) {
    res.status(404).send(e.message)
  }
})

router.delete('/:id', async function(req, res) {
  const id = req.params.id
  await usersService.deleteUser(id)
  return res.status(204).json('successfully deleted')
})

export default router
