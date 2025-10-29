package com.finn.service

import com.finn.dto.PostDto
import com.finn.entity.Like
import com.finn.entity.Post
import com.finn.entity.UserCommunity
import com.finn.exception.ConflictException
import com.finn.exception.NotFoundException
import com.finn.repository.LikeRepository
import com.finn.repository.PostRepository
import com.finn.repository.UserCommunityRepository
import com.finn.service.impl.PostServiceImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.Optional

class PostServiceImplTest {
    private val postRepository: PostRepository = mockk(relaxed = true)
    private val likeRepository: LikeRepository = mockk(relaxed = true)
    private val userCommunityRepository: UserCommunityRepository = mockk(relaxed = true)
    private val service = PostServiceImpl(postRepository, likeRepository, userCommunityRepository)

    @Test
    fun `getFeed returns empty when user subscribed to nothing`() {
        every { userCommunityRepository.findAllByUserId("user") } returns emptyList()

        val result = service.getFeed("user", page = 1)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getFeed normalizes page index`() {
        every { userCommunityRepository.findAllByUserId("user") } returns listOf(UserCommunity(id = 1, userId = "user", communityId = 10))
        every {
            postRepository.findAllByCommunityIdInOrderByIdDesc(any(), PageRequest.of(0, 20))
        } returns PageImpl(listOf(Post(id = 5, content = "hello", userId = "user", communityId = 10)))

        val result = service.getFeed("user", page = 0)

        assertEquals(1, result.size)
        assertEquals("hello", result.first().content)
    }

    @Test
    fun `getOne throws when missing`() {
        every { postRepository.findById(5) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) { service.getOne(5) }
    }

    @Test
    fun `create persists dto`() {
        val dto = PostDto(id = null, content = "new", image = null, userId = "u", communityId = 2)
        val savedPost = slot<Post>()
        every { postRepository.save(capture(savedPost)) } answers { savedPost.captured.copy(id = 22) }

        val created = service.create(dto)

        assertEquals("new", created.content)
        verify { postRepository.save(match { it.userId == "u" && it.communityId == 2L }) }
    }

    @Test
    fun `giveLike throws on duplicate`() {
        every { likeRepository.findByUserIdAndPostId("u", 7) } returns Like(id = 1, userId = "u", postId = 7)

        assertThrows(ConflictException::class.java) { service.giveLike("u", 7) }
    }

    @Test
    fun `giveLike saves when new`() {
        every { likeRepository.findByUserIdAndPostId("u", 7) } returns null
        val savedLike = slot<Like>()
        every { likeRepository.save(capture(savedLike)) } answers { savedLike.captured.copy(id = 33) }

        val dto = service.giveLike("u", 7)

        assertEquals("u", dto.userId)
        verify { likeRepository.save(match { it.userId == "u" && it.postId == 7L }) }
    }

    @Test
    fun `removeLike deletes when present`() {
        every { likeRepository.findByUserIdAndPostId("u", 8) } returns Like(id = 2, userId = "u", postId = 8)

        service.removeLike("u", 8)

        verify { likeRepository.delete(match { it.postId == 8L }) }
    }

    @Test
    fun `removeLike is no-op when missing`() {
        every { likeRepository.findByUserIdAndPostId("u", 8) } returns null

        service.removeLike("u", 8)

        verify(exactly = 0) { likeRepository.delete(any()) }
    }

    @Test
    fun `hasLike returns boolean`() {
        every { likeRepository.findByUserIdAndPostId("u", 9) } returns null
        assertFalse(service.hasLike("u", 9))
    }
}
