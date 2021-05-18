const usersData = require('../data/usersData')

module.exports = {
    getUsers() {
        return usersData.getUsers()
    },

    saveUser(user) {
        return usersData.saveUser(user)
    },

    getSingleUser(id) {
        return usersData.getSingleUser(id)
    },

    updateUser(user, id) {
        return usersData.updateUser(user, id)
    },

    deleteUser(id) {
        return usersData.deleteUser(id)
    }
}