const express = require("express")
const router = express.Router()
const usersService = require('../service/usersService')

router.get("/", async function(req, res) {
    const users = await usersService.getUsers()
    res.json(users)
})

router.get("/:id", async function(req, res) {
    const id = req.params.id
    const foundUser = await usersService.getSingleUser(id)
    res.json(foundUser)
})

router.post("/", async function(req, res) {
    const user = req.body // id, email, name, password
    const newUser = await usersService.saveUser(user)
    res.json(newUser)
})

router.put('/:id', async function(req, res) {
    const id = req.params.id
    const user = req.body
    const updatedUser = await usersService.updateUser(user, id)
    res.json(updatedUser)
})

router.delete('/:id', async function(req, res) {
    const id = req.params.id
    try {
        await usersService.deleteUser(id)
    } catch(err) {
        return res.status(502).json("error on delete")
    }
    return res.status(200).json("successfully deleted")
})

module.exports = router

