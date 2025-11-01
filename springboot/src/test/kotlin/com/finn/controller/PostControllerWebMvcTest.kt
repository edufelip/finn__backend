package com.finn.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.finn.dto.LikeDto
import com.finn.dto.PostDto
import com.finn.exception.UploadValidationException
import com.finn.service.PostService
import com.finn.storage.StorageService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PostController::class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerWebMvcTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var postService: PostService

    @MockBean
    private lateinit var storageService: StorageService

    @Test
    fun `create post returns 201`() {
        val request = PostDto(id = null, content = "Hello", image = null, userId = "user", communityId = 2)
        whenever(postService.create(any<PostDto>())).thenReturn(request.copy(id = 10))

        mockMvc.perform(
            post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(10))
    }

    @Test
    fun `create multipart post stores file`() {
        val createDto = PostDto(id = null, content = "Hello", image = null, userId = "user", communityId = 2)
        val jsonPayload = objectMapper.writeValueAsString(createDto)
        val filePart = MockMultipartFile("post", "image.png", MediaType.IMAGE_PNG_VALUE, ByteArray(10))
        whenever(storageService.storePngWithChecks(any())).thenReturn("image.png")
        doAnswer { invocation ->
            val dto = invocation.getArgument<PostDto>(0)
            dto.copy(id = 11)
        }
            .whenever(postService)
            .create(any())

        mockMvc.perform(
            multipart("/posts")
                .file(filePart)
                .param("post", jsonPayload),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.image").value("image.png"))
    }

    @Test
    fun `unlike without user id returns bad request`() {
        mockMvc.perform(
            post("/posts/likes/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `like endpoint returns created`() {
        whenever(postService.giveLike(eq("user"), eq(7L))).thenReturn(LikeDto(id = 1, userId = "user", postId = 7))

        mockMvc.perform(
            post("/posts/likes")
                .param("user_id", "user")
                .param("post_id", "7"),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.userId").value("user"))
    }

    @Test
    fun `create multipart with non png returns 500`() {
        whenever(storageService.storePngWithChecks(any())).thenThrow(UploadValidationException("File must be .png"))

        val payload = objectMapper.writeValueAsString(PostDto(id = null, content = "bad", image = null, userId = "user", communityId = 1))
        val request =
            multipart("/posts")
                .file(MockMultipartFile("post", "post.jpg", MediaType.IMAGE_JPEG_VALUE, ByteArray(10)))
                .param("post", payload)

        mockMvc.perform(request)
            .andExpect(status().isInternalServerError)
            .andExpect(content().string("File must be .png"))
    }

    @Test
    fun `create multipart with oversized file returns 500`() {
        whenever(storageService.storePngWithChecks(any())).thenThrow(UploadValidationException("File too large"))

        val payload = objectMapper.writeValueAsString(PostDto(id = null, content = "large", image = null, userId = "user", communityId = 1))
        val request =
            multipart("/posts")
                .file(MockMultipartFile("post", "post.png", MediaType.IMAGE_PNG_VALUE, ByteArray(400 * 1024)))
                .param("post", payload)

        mockMvc.perform(request)
            .andExpect(status().isInternalServerError)
            .andExpect(content().string("File too large"))
    }
}
