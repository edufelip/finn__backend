package com.finn.controller

import com.finn.dto.CommunityDto
import com.finn.dto.PostDto
import com.finn.dto.UserDto
import com.finn.service.CommunityService
import com.finn.service.PostService
import com.finn.service.UserService
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class PostControllerLikesAndPagingTest {
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

    lateinit var userId: String
    var communityId: Long = 0

    @BeforeEach
    fun setup() {
        userId = "user-likes"
        userService.createUser(UserDto(id = userId, name = "U"))
        val comm = communityService.create(CommunityDto(id = null, title = "LikesComm", description = "D", userId = userId, image = null))
        communityId = comm.id!!
    }

    @Test
    fun likes_endpoints_flow() {
        val post = postService.create(PostDto(id = null, content = "hello", image = null, userId = userId, communityId = communityId))

        mockMvc.perform(get("/posts/{id}/likes", post.id!!))
            .andExpect(status().isOk)
            .andExpect(content().string("0"))

        mockMvc.perform(post("/posts/likes").param("user_id", userId).param("post_id", post.id.toString()))
            .andExpect(status().isCreated)

        mockMvc.perform(get("/posts/{id}/likes", post.id))
            .andExpect(status().isOk)
            .andExpect(content().string("1"))

        mockMvc.perform(get("/posts/{postId}/users/{userId}", post.id, userId))
            .andExpect(status().isOk)
            .andExpect(content().string("1"))

        val json = "{" + "\"id\":\"$userId\"" + "}"
        mockMvc.perform(post("/posts/likes/{id}", post.id).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isNoContent)

        mockMvc.perform(get("/posts/{id}/likes", post.id))
            .andExpect(status().isOk)
            .andExpect(content().string("0"))

        mockMvc.perform(get("/posts/{postId}/users/{userId}", post.id, userId))
            .andExpect(status().isOk)
            .andExpect(content().string("0"))
    }

    @Test
    fun posts_by_community_paging() {
        for (i in 1..25) {
            postService.create(PostDto(id = null, content = "c$i", image = null, userId = userId, communityId = communityId))
        }

        mockMvc.perform(get("/posts/communities/{id}", communityId).param("page", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(20)))

        mockMvc.perform(get("/posts/communities/{id}", communityId).param("page", "2"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(5)))
    }
}
