const pgp = require('pg-promise')({})
require('dotenv').config()

const dbData = {
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  database: process.env.DB_NAME,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD
}

const database = pgp(dbData)

export default database
