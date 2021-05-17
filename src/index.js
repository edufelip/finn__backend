const express = require("express")
const app = express()

app.get("/", (req, res) => {
    res.json("DEU BOM")
})

app.listen(process.env.PORT || 3000, () => {
    console.log("the app is running")
})