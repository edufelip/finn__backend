package com.finn

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.finn.dto.CommentDto
import com.finn.dto.CommunityDto
import com.finn.dto.PostDto
import com.finn.dto.UserDto
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationEndToEndTest {
    companion object {
        @Container
        @JvmStatic
        private val postgres = PostgreSQLContainer("postgres:15-alpine")

        private val uploadDir: Path = Files.createTempDirectory("finn-upload-test")

        @JvmStatic
        @DynamicPropertySource
        fun configure(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("app.upload.dir") { uploadDir.toAbsolutePath().toString() }
        }

        @AfterAll
        @JvmStatic
        fun cleanupUploadDir() {
            if (Files.exists(uploadDir)) {
                Files.walk(uploadDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach { Files.deleteIfExists(it) }
            }
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `full user community post lifecycle succeeds`() {
        postJson("/users", UserDto(id = "user-e2e", name = "E2E"))

        val communityPayload =
            CommunityDto(
                id = null,
                title = "E2E",
                description = "End to End",
                image = null,
                userId = "user-e2e",
            )
        val communityResponse = postJson("/communities", communityPayload)
        val communityId = communityResponse["id"].asLong()

        mockMvc.perform(
            post("/communities/subscribe")
                .param("user_id", "user-e2e")
                .param("community_id", communityId.toString()),
        )
            .andExpect(status().isCreated)

        val postPayload =
            PostDto(
                id = null,
                content = "content",
                image = null,
                userId = "user-e2e",
                communityId = communityId,
            )
        val postResponse = postJson("/posts", postPayload)
        val postId = postResponse["id"].asLong()

        val commentPayload =
            CommentDto(
                id = null,
                content = "great",
                userId = "user-e2e",
                postId = postId,
            )
        val commentResponse = postJson("/comments", commentPayload)
        val commentId = commentResponse["id"].asLong()

        val updatedComment =
            CommentDto(
                id = commentId,
                content = "even better",
                userId = "user-e2e",
                postId = postId,
            )
        mockMvc.perform(
            put("/comments/$commentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(updatedComment)),
        )
            .andExpect(status().isNoContent)

        mockMvc.perform(get("/comments/$commentId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").value("even better"))

        val likeResponse =
            mockMvc.perform(
                post("/posts/likes")
                    .param("user_id", "user-e2e")
                    .param("post_id", postId.toString()),
            )
                .andExpect(status().isCreated)
                .andReturn()
        val likeId = objectMapper.readTree(likeResponse.response.contentAsString)["id"].asLong()

        mockMvc.perform(get("/posts/$postId/likes"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").value("1"))

        mockMvc.perform(
            post("/posts/likes/$likeId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"user-e2e\"}"),
        )
            .andExpect(status().isNoContent)

        mockMvc.perform(get("/posts/$postId/likes"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").value("0"))

        val communityWithImage =
            CommunityDto(
                id = null,
                title = "E2EImage",
                description = "With image",
                image = null,
                userId = "user-e2e",
            )
        val communityMultipart =
            multipart("/communities")
                .file(
                    MockMultipartFile(
                        "community",
                        "community.png",
                        MediaType.IMAGE_PNG_VALUE,
                        ByteArray(32) { 1 },
                    ),
                )
                .param("community", objectMapper.writeValueAsString(communityWithImage))
        val communityMultipartResult =
            mockMvc.perform(communityMultipart)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.image").isNotEmpty)
                .andReturn()
        val communityImage = objectMapper.readTree(communityMultipartResult.response.contentAsString)["image"].asText()
        assertTrue(Files.exists(uploadDir.resolve(communityImage)))

        val postWithImage =
            PostDto(
                id = null,
                content = "content with image",
                image = null,
                userId = "user-e2e",
                communityId = communityId,
            )
        val postMultipart =
            multipart("/posts")
                .file(
                    MockMultipartFile(
                        "post",
                        "post.png",
                        MediaType.IMAGE_PNG_VALUE,
                        ByteArray(48) { 2 },
                    ),
                )
                .param("post", objectMapper.writeValueAsString(postWithImage))
        val postMultipartResult =
            mockMvc.perform(postMultipart)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.image").isNotEmpty)
                .andReturn()
        val postImage = objectMapper.readTree(postMultipartResult.response.contentAsString)["image"].asText()
        assertTrue(Files.exists(uploadDir.resolve(postImage)))

        mockMvc.perform(get("/posts/users/user-e2e/feed"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(postId))

        val subscribersBefore =
            mockMvc.perform(get("/communities/$communityId/subscribers"))
                .andExpect(status().isOk)
                .andReturn()
        assertEquals("1", subscribersBefore.response.contentAsString)

        mockMvc.perform(
            post("/communities/unsubscribe")
                .param("user_id", "user-e2e")
                .param("community_id", communityId.toString()),
        )
            .andExpect(status().isNoContent)

        val subscribersAfter =
            mockMvc.perform(get("/communities/$communityId/subscribers"))
                .andExpect(status().isOk)
                .andReturn()
        assertEquals("0", subscribersAfter.response.contentAsString)
    }

    private fun postJson(
        url: String,
        payload: Any,
    ): JsonNode {
        val result =
            mockMvc.perform(
                post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(payload)),
            )
                .andExpect(status().isCreated)
                .andReturn()

        return objectMapper.readTree(result.response.contentAsString)
    }
}
