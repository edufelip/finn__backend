
import express from 'express'
import userRoutes from './routes/userRoutes'
import postRoutes from './routes/postRoutes'
import communityRoutes from './routes/communityRoutes'
import commentRoutes from './routes/commentRoutes'

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
      if (error.message.match(/\balready exists$/)) {
        res.status(409).send(error.message)
      }
      if (error.message.match(/\bnot found$/)) {
        res.status(404).send(error.message)
      }
      res.status(500).send(error.message)
    })
  }

  setRoutes() {
    this.express.use('/users', userRoutes)
    this.express.use('/posts', postRoutes)
    this.express.use('/communities', communityRoutes)
    this.express.use('/comments', commentRoutes)
  }
}

const app = new MainApp().express
export default app
