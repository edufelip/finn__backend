package com.finn

import com.finn.dto.*
import com.finn.service.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.mock.web.MockMultipartFile

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiParityTests {

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15-alpine")

        @JvmStatic
        @DynamicPropertySource
        fun registerProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }
    }

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var communityService: CommunityService
    @Autowired lateinit var postService: PostService
    @Autowired lateinit var commentService: CommentService

    lateinit var userId: String
    var communityId: Long = 0
    var postId: Long = 0
    var commentId: Long = 0

    @BeforeEach
    fun seed() {
        userId = "user-parity"
        userService.createUser(UserDto(id = userId, name = "Tester"))
        val comm = communityService.create(CommunityDto(id = null, title = "Parity", description = "Desc", userId = userId, image = null))
        communityId = comm.id!!
        postId = postService.create(PostDto(id = null, content = "hello world", image = null, userId = userId, communityId = communityId)).id!!
        commentId = commentService.create(CommentDto(id = null, content = "nice", userId = userId, postId = postId)).id!!
    }

    @Test
    fun get_post_not_found_returns_404() {
        mockMvc.perform(get("/posts/{id}", postId + 999))
            .andExpect(status().isNotFound)
    }

    @Test
    fun invalid_page_defaults_and_returns_200() {
        communityService.subscribe(userId, communityId)
        mockMvc.perform(get("/posts/users/{id}/feed", userId).param("page", "a"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Int>(greaterThanOrEqualTo(1))))
    }

    @Test
    fun comments_by_wrong_post_returns_empty_array() {
        mockMvc.perform(get("/comments/posts/{id}", postId + 999))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize<Int>(0)))
    }

    @Test
    fun subscribers_wrong_community_returns_404() {
        mockMvc.perform(get("/communities/{id}/subscribers", communityId + 999))
            .andExpect(status().isNotFound)
    }

    @Test
    fun comment_validation_too_long_returns_400() {
        val longContent = (1..210).joinToString("") { "x" }
        val body = "{" +
                "\"content\":\"$longContent\"," +
                "\"userId\":\"$userId\"," +
                "\"postId\":$postId" +
                "}"
        mockMvc.perform(post("/comments").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun community_create_with_png_succeeds_and_file_saved() {
        val bytes = ByteArray(1024) { 0x00 }
        val payload = "{" +
                "\"title\":\"random title\"," +
                "\"description\":\"random description\"," +
                "\"userId\":\"$userId\"" +
                "}"
        val jsonPart = MockMultipartFile("community", "community", "application/json", payload.toByteArray())
        val filePart = MockMultipartFile("community", "test.png", "image/png", bytes)

        val result = mockMvc.perform(multipart("/communities").file(filePart).file(jsonPart))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.image", notNullValue()))
            .andReturn()
        val body = result.response.contentAsString
        val image = Regex("\"image\":\"([^\"]+)\"").find(body)?.groupValues?.get(1)
        if (image != null) {
            File("../public/$image").delete()
        }
    }

    @Test
    fun community_create_wrong_ext_returns_500_text() {
        val bytes = ByteArray(1024) { 0x01 }
        val payload = "{" +
                "\"title\":\"random title\"," +
                "\"description\":\"random description\"," +
                "\"userId\":\"$userId\"" +
                "}"
        val jsonPart = MockMultipartFile("community", "community", "application/json", payload.toByteArray())
        val filePart = MockMultipartFile("community", "test.jpg", "image/jpeg", bytes)

        mockMvc.perform(multipart("/communities").file(filePart).file(jsonPart))
            .andExpect(status().isInternalServerError)
            .andExpect(content().string("File must be .png"))
    }

    @Test
    fun community_create_large_file_returns_500_text() {
        val bytes = ByteArray(350 * 1024) { 0x02 }
        val payload = "{" +
                "\"title\":\"random title\"," +
                "\"description\":\"random description\"," +
                "\"userId\":\"$userId\"" +
                "}"
        val jsonPart = MockMultipartFile("community", "community", "application/json", payload.toByteArray())
        val filePart = MockMultipartFile("community", "testLarge.png", "image/png", bytes)

        mockMvc.perform(multipart("/communities").file(filePart).file(jsonPart))
            .andExpect(status().isInternalServerError)
            .andExpect(content().string("File too large"))
    }

    @Test
    fun community_update_image_wrong_ext_returns_500_text() {
        val comm = communityService.create(CommunityDto(id = null, title = "UpImg", description = "Desc", userId = userId, image = null))
        val bytes = ByteArray(1024) { 0x03 }
        val filePart = MockMultipartFile("community", "test.jpg", "image/jpeg", bytes)
        mockMvc.perform(multipart("/communities/{id}/image", comm.id!!).file(filePart))
            .andExpect(status().isInternalServerError)
            .andExpect(content().string("File must be .png"))
    }

    @Test
    fun community_update_image_large_returns_500_text() {
        val comm = communityService.create(CommunityDto(id = null, title = "UpLarge", description = "Desc", userId = userId, image = null))
        val bytes = ByteArray(350 * 1024) { 0x04 }
        val filePart = MockMultipartFile("community", "testLarge.png", "image/png", bytes)
        mockMvc.perform(multipart("/communities/{id}/image", comm.id!!).file(filePart))
            .andExpect(status().isInternalServerError)
            .andExpect(content().string("File too large"))
    }

    @Test
    fun posts_create_with_png_succeeds() {
        val bytes = ByteArray(1024) { 0x05 }
        val payload = "{" +
                "\"content\":\"new content\"," +
                "\"userId\":\"$userId\"," +
                "\"communityId\":$communityId" +
                "}"
        val jsonPart = MockMultipartFile("post", "post", "application/json", payload.toByteArray())
        val filePart = MockMultipartFile("post", "test.png", "image/png", bytes)

        val result = mockMvc.perform(multipart("/posts").file(filePart).file(jsonPart))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.image", notNullValue()))
            .andReturn()
        val body = result.response.contentAsString
        val image = Regex("\"image\":\"([^\"]+)\"").find(body)?.groupValues?.get(1)
        if (image != null) {
            File("../public/$image").delete()
        }
    }

    @Test
    fun community_subscribe_and_unsubscribe_endpoints() {
        val payload = "{" +
                "\"user_id\":\"$userId\"," +
                "\"community_id\":$communityId" +
                "}"
        mockMvc.perform(post("/communities/subscribe").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated)

        // Should be subscribed
        mockMvc.perform(get("/communities/{communityId}/users/{userId}", communityId, userId))
            .andExpect(status().isOk)

        // Unsubscribe
        mockMvc.perform(post("/communities/unsubscribe").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isNoContent)
    }
}
