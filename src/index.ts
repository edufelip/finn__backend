
import express from 'express'
import userRoutes from './routes/userRoutes'

class MainApp {
  public express

  constructor() {
    this.express = express()
    this.middlewares()
    this.setRoutes()
  }

  middlewares() {
    this.express.use(express.json())
  }

  setRoutes() {
    this.express.use('/users', userRoutes)
  }
}

const app = new MainApp().express
export default app
