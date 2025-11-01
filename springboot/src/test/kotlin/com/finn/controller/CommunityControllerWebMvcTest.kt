package com.finn.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.finn.dto.CommunityDto
import com.finn.exception.ConflictException
import com.finn.exception.UploadValidationException
import com.finn.service.CommunityService
import com.finn.storage.StorageService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
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

@WebMvcTest(CommunityController::class)
@AutoConfigureMockMvc(addFilters = false)
class CommunityControllerWebMvcTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var communityService: CommunityService

    @MockBean
    private lateinit var storageService: StorageService

    @Test
    fun `create returns created community`() {
        val requestDto = CommunityDto(id = null, title = "Dev", description = "Desc", image = null, userId = "user")
        val responseDto = requestDto.copy(id = 42)
        whenever(communityService.create(any<CommunityDto>())).thenReturn(responseDto)

        mockMvc.perform(
            post("/communities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.title").value("Dev"))
    }

    @Test
    fun `create returning conflict bubbles error`() {
        val requestDto = CommunityDto(id = null, title = "Dev", description = "Desc", image = null, userId = "user")
        whenever(communityService.create(any<CommunityDto>())).thenThrow(ConflictException("exists"))

        mockMvc.perform(
            post("/communities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto)),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.error").value("Conflict"))
    }

    @Test
    fun `create multipart stores file and delegates to service`() {
        val createDto = CommunityDto(id = null, title = "Dev", description = "Desc", image = null, userId = "user")
        val jsonPayload = objectMapper.writeValueAsString(createDto)
        val filePart = MockMultipartFile("community", "logo.png", MediaType.IMAGE_PNG_VALUE, ByteArray(10))
        whenever(storageService.storePngWithChecks(any())).thenReturn("logo.png")
        doAnswer { invocation ->
            val dto = invocation.getArgument<CommunityDto>(0)
            dto.copy(id = 7)
        }
            .whenever(communityService)
            .create(any())

        mockMvc.perform(
            multipart("/communities")
                .file(filePart)
                .param("community", jsonPayload),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.image").value("logo.png"))
    }

    @Test
    fun `create multipart missing json payload returns bad request`() {
        val filePart = MockMultipartFile("community", "logo.png", MediaType.IMAGE_PNG_VALUE, ByteArray(10))

        mockMvc.perform(
            multipart("/communities")
                .file(filePart),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("missing community payload"))
    }

    @Test
    fun `create multipart with non png returns 500`() {
        whenever(storageService.storePngWithChecks(any())).thenThrow(UploadValidationException("File must be .png"))

        val payload =
            objectMapper.writeValueAsString(
                CommunityDto(id = null, title = "Invalid", description = "Desc", image = null, userId = "user"),
            )

        mockMvc.perform(
            multipart("/communities")
                .file(MockMultipartFile("community", "logo.jpg", MediaType.IMAGE_JPEG_VALUE, ByteArray(10)))
                .param("community", payload),
        )
            .andExpect(status().isInternalServerError)
            .andExpect(content().string("File must be .png"))
    }

    @Test
    fun `create multipart with oversized file returns 500`() {
        whenever(storageService.storePngWithChecks(any())).thenThrow(UploadValidationException("File too large"))

        val payload =
            objectMapper.writeValueAsString(
                CommunityDto(id = null, title = "Big", description = "Desc", image = null, userId = "user"),
            )

        mockMvc.perform(
            multipart("/communities")
                .file(MockMultipartFile("community", "logo.png", MediaType.IMAGE_PNG_VALUE, ByteArray(400 * 1024)))
                .param("community", payload),
        )
            .andExpect(status().isInternalServerError)
            .andExpect(content().string("File too large"))
    }
}
