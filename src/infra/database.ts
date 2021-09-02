const pgp = require('pg-promise')({})
require('dotenv').config()

const dbName = process.env.TEST_MODE === 'true' ? process.env.DEVDB_NAME : process.env.DB_NAME
const dbData = {
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  database: dbName,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD
}

const database = pgp(dbData)

export default database
