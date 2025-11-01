package com.finn.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class UserControllerValidationTest {
    @Autowired lateinit var mockMvc: MockMvc

    @Test
    fun `missing name yields 400`() {
        val body =
            "{" +
                "\"id\":\"user-x\"" +
                "}"
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest)
    }
}
