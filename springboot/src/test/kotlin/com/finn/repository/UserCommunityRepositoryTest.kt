package com.finn.repository

import com.finn.entity.Community
import com.finn.entity.User
import com.finn.entity.UserCommunity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DataJpaIntegrationTest
class UserCommunityRepositoryTest : RepositoryIntegrationTestBase() {
    @Autowired
    private lateinit var userCommunityRepository: UserCommunityRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var communityRepository: CommunityRepository

    @Test
    fun `countByCommunityId reflects subscriptions`() {
        val owner = userRepository.save(User(id = "owner", name = "Owner"))
        val subscriber = userRepository.save(User(id = "subscriber", name = "Subscriber"))
        val community = communityRepository.save(Community(title = "Testing", description = "Desc", userId = owner.id))

        val communityId = requireNotNull(community.id)

        assertEquals(0, userCommunityRepository.countByCommunityId(communityId))

        userCommunityRepository.save(UserCommunity(userId = subscriber.id, communityId = communityId))

        assertEquals(1, userCommunityRepository.countByCommunityId(communityId))
    }

    @Test
    fun `findByUserIdAndCommunityId returns existing link`() {
        val owner = userRepository.save(User(id = "owner-2", name = "Owner"))
        val user = userRepository.save(User(id = "member", name = "Member"))
        val community = communityRepository.save(Community(title = "Verifiers", description = "Desc", userId = owner.id))
        val communityId = requireNotNull(community.id)
        val link = userCommunityRepository.save(UserCommunity(userId = user.id, communityId = communityId))

        val found = userCommunityRepository.findByUserIdAndCommunityId(user.id, communityId)

        assertNotNull(found)
        assertEquals(link.id, found?.id)
    }
}
