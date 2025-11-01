package com.finn

import com.finn.dto.*
import com.finn.service.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTests {
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

    @Autowired lateinit var userService: UserService

    @Autowired lateinit var communityService: CommunityService

    @Autowired lateinit var postService: PostService

    @Autowired lateinit var commentService: CommentService

    @Test
    fun user_crud_and_lookup() {
        val created = userService.createUser(UserDto(id = "user-1", name = "Alice"))
        assertEquals("user-1", created.id)
        assertEquals("Alice", userService.getUser("user-1").name)
        userService.updateUser("user-1", "Alice Doe")
        assertEquals("Alice Doe", userService.getUser("user-1").name)
    }

    @Test
    fun community_subscription_and_counts() {
        userService.createUser(UserDto(id = "user-2", name = "Bob"))
        val comm = communityService.create(CommunityDto(id = null, title = "Kotlin", description = "Lang", userId = "user-2", image = null))
        assertEquals(0, communityService.subscribersCount(comm.id!!))
        communityService.subscribe("user-2", comm.id!!)
        assertEquals(1, communityService.subscribersCount(comm.id!!))
        assertNotNull(communityService.subscription("user-2", comm.id!!))
        communityService.unsubscribe("user-2", comm.id!!)
        assertEquals(0, communityService.subscribersCount(comm.id!!))
    }

    @Test
    fun posts_and_likes_flow() {
        userService.createUser(UserDto(id = "user-3", name = "Carol"))
        val comm =
            communityService.create(
                CommunityDto(id = null, title = "Android", description = "Mobile", userId = "user-3", image = null),
            )
        communityService.subscribe("user-3", comm.id!!)
        val post = postService.create(PostDto(id = null, content = "hello", image = null, userId = "user-3", communityId = comm.id!!))
        assertEquals(0, postService.likeCount(post.id!!))
        postService.giveLike("user-3", post.id!!)
        assertTrue(postService.hasLike("user-3", post.id!!))
        assertEquals(1, postService.likeCount(post.id!!))
        postService.removeLike("user-3", post.id!!)
        assertFalse(postService.hasLike("user-3", post.id!!))
    }

    @Test
    fun comments_flow() {
        userService.createUser(UserDto(id = "user-4", name = "Dave"))
        val comm = communityService.create(CommunityDto(id = null, title = "Compose", description = "UI", userId = "user-4", image = null))
        val post = postService.create(PostDto(id = null, content = "post", image = null, userId = "user-4", communityId = comm.id!!))
        val c = commentService.create(CommentDto(id = null, content = "nice", userId = "user-4", postId = post.id!!))
        assertEquals("nice", commentService.getOne(c.id!!).content)
        assertTrue(commentService.getByPost(post.id!!).any { it.id == c.id })
        commentService.update(c.id!!, c.copy(content = "great"))
        assertEquals("great", commentService.getOne(c.id!!).content)
        commentService.delete(c.id!!)
    }
}
