
import express from 'express'
import userRoutes from './routes/userRoutes'
import postRoutes from './routes/postRoutes'

class MainApp {
  public express

  constructor() {
    this.express = express()
    this.express.use(express.json())
    this.setRoutes()
    this.middlewares()
  }

  middlewares() {
    this.express.use(function(error, req, res, next) {
      if (error.message === 'User already exists') {
        res.status(409).send(error.message)
      }
      if (error.message === 'User not found') {
        res.status(404).send(error.message)
      }
      res.status(500).send(error.message)
    })
  }

  setRoutes() {
    this.express.use('/users', userRoutes)
    this.express.use('/posts', postRoutes)
  }
}

const app = new MainApp().express
export default app
