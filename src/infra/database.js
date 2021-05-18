const pgp = require('pg-promise')({})

const dbData = {
    host: 'tuffi.db.elephantsql.com',
    port: '5432',
    database: 'shqgqrnp',
    user: "shqgqrnp",
    password: 'mWeZzMW1fQ3rmQBybBojLk5xbefwoISf'
}

const db = pgp(dbData)

module.exports = db