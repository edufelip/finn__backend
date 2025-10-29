package com.finn.repository

import com.finn.entity.Community
import com.finn.entity.Post
import com.finn.entity.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

@DataJpaIntegrationTest
class PostRepositoryTest : RepositoryIntegrationTestBase() {
    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var communityRepository: CommunityRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `findAllByCommunityIdOrderByIdDesc returns newest first`() {
        val user = userRepository.save(User(id = "user-1", name = "Alice"))
        val community = communityRepository.save(Community(title = "Kotlin", description = "Lang", userId = user.id))
        val otherCommunity = communityRepository.save(Community(title = "Java", description = "Lang", userId = user.id))

        val communityId = requireNotNull(community.id)
        val otherCommunityId = requireNotNull(otherCommunity.id)

        val postOld = postRepository.save(Post(content = "old", userId = user.id, communityId = communityId))
        val postNew = postRepository.save(Post(content = "new", userId = user.id, communityId = communityId))
        postRepository.save(Post(content = "other", userId = user.id, communityId = otherCommunityId))

        val results = postRepository.findAllByCommunityIdOrderByIdDesc(communityId, PageRequest.of(0, 10))

        assertEquals(listOf(postNew.id, postOld.id), results.content.map { it.id })
    }

    @Test
    fun `findAllByCommunityIdInOrderByIdDesc returns feed`() {
        val user = userRepository.save(User(id = "user-2", name = "Bob"))
        val community1 = communityRepository.save(Community(title = "Compose", description = "UI", userId = user.id))
        val community2 = communityRepository.save(Community(title = "Spring", description = "Backend", userId = user.id))

        val community1Id = requireNotNull(community1.id)
        val community2Id = requireNotNull(community2.id)

        val post1 = postRepository.save(Post(content = "first", userId = user.id, communityId = community1Id))
        val post2 = postRepository.save(Post(content = "second", userId = user.id, communityId = community2Id))
        val post3 = postRepository.save(Post(content = "third", userId = user.id, communityId = community1Id))

        val results = postRepository.findAllByCommunityIdInOrderByIdDesc(setOf(community1Id, community2Id), PageRequest.of(0, 10))

        assertEquals(listOf(post3.id, post2.id, post1.id), results.content.map { it.id })
    }
}
