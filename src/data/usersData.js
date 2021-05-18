const database = require('../infra/database')

module.exports = {
    getUsers() {
        return database.query('SELECT * FROM users')
    },

    saveUser(user) {
        return database.one('INSERT INTO users(id, name, email, password) VALUES(${id} ,${name}, ${email}, ${password}) RETURNING id, name, email', 
        user)
    },

    getSingleUser(id) {
        return database.one('SELECT * FROM users WHERE id = ${id}', 
        {id: id})
    },

    updateUser(user, id) {
        return database.one('UPDATE users SET password=${password} WHERE id = ${id} RETURNING id, name, email', 
        {...user, id: id})
    },

    deleteUser(id) {
        return database.none('DELETE FROM users WHERE id = ${id}', 
        {id: id})
    }

}
