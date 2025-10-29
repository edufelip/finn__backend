package com.finn.service

import com.finn.dto.CommentDto
import com.finn.entity.Comment
import com.finn.exception.NotFoundException
import com.finn.repository.CommentRepository
import com.finn.service.impl.CommentServiceImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.Optional

class CommentServiceImplTest {
    private val commentRepository: CommentRepository = mockk(relaxed = true)
    private val service = CommentServiceImpl(commentRepository)

    @Test
    fun `getByPost maps entities to dtos`() {
        every { commentRepository.findAllByPostIdOrderByIdDesc(1) } returns
            listOf(
                Comment(id = 10, content = "Nice", userId = "u", postId = 1),
            )

        val result = service.getByPost(1)

        assertEquals(1, result.size)
        assertEquals("Nice", result.first().content)
    }

    @Test
    fun `getOne throws when missing`() {
        every { commentRepository.findById(42) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) { service.getOne(42) }
    }

    @Test
    fun `create delegates to repository`() {
        val dto = CommentDto(id = null, content = "Hi", userId = "u", postId = 2)
        val saved = slot<Comment>()
        every { commentRepository.save(capture(saved)) } answers { saved.captured.copy(id = 25) }

        val created = service.create(dto)

        assertEquals("Hi", created.content)
        verify { commentRepository.save(match { it.userId == "u" && it.postId == 2L }) }
    }

    @Test
    fun `update persists new content`() {
        val entity = Comment(id = 5, content = "Old", userId = "u", postId = 2)
        every { commentRepository.findById(5) } returns Optional.of(entity)
        every { commentRepository.save(any<Comment>()) } answers { it.invocation.args[0] as Comment }

        service.update(5, CommentDto(id = 5, content = "New", userId = "u", postId = 2))

        verify { commentRepository.save(match { it.id == 5L && it.content == "New" }) }
    }

    @Test
    fun `delete delegates to repository`() {
        service.delete(99)

        verify { commentRepository.deleteById(99) }
    }
}
