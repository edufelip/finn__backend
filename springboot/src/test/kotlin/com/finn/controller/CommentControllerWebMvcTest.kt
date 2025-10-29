package com.finn.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.finn.dto.CommentDto
import com.finn.exception.NotFoundException
import com.finn.service.CommentService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CommentController::class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerWebMvcTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var commentService: CommentService

    @Test
    fun `create comment returns 201`() {
        val request = CommentDto(id = null, content = "Great", userId = "user", postId = 2)
        whenever(commentService.create(any<CommentDto>())).thenReturn(request.copy(id = 5))

        mockMvc.perform(
            post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(5))
    }

    @Test
    fun `create comment with missing content fails validation`() {
        val invalidJson = "{" + "\"userId\":\"u\"," + "\"postId\":1}" // missing content field

        mockMvc.perform(
            post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `get one comment not found returns 404`() {
        whenever(commentService.getOne(any())).thenThrow(NotFoundException("missing"))

        mockMvc.perform(get("/comments/10"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("missing"))
    }
}
