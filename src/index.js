const express = require('express')
const app = express()
const userRoutes = require('./routes/userRoutes')

app.use(express.json())
app.use('/users', userRoutes)

app.listen(process.env.PORT || 3000, () => {
    console.log('the app is running')
})