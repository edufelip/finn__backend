const axios = require('axios')

test('Should get users', async function() {
    const response = await axios({
        url: 'http://localhost:3000/users',
        method: 'get'
    })
    const users = response.data
    expect(users).not.toBeNull()
})

test('should create user', async function() {
    const response = await axios({
        url: 'http://localhost:3000/users',
        method: 'post',
        data: {
            id: "12313123123",
            name: "leandro",
            email: "leandro_baleia@gmail.com",
            password: "123123"
        }
    })
    const user = response.data
    expect(user).toEqual({
            "id": "12313123123",
            "name": "leandro",
            "email": "leandro_baleia@gmail.com"
        }
    )
})