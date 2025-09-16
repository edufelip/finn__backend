package com.finn.mapper

import com.finn.dto.*
import com.finn.entity.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MappersTest {

    @Test
    fun `user entity to dto and back`() {
        val entity = User(id = "u1", name = "Alice", photo = "p.png")
        val dto = entity.toDto()
        assertEquals("u1", dto.id)
        assertEquals("Alice", dto.name)
        assertEquals("p.png", dto.photo)

        val entity2 = dto.toEntity()
        assertEquals(entity.id, entity2.id)
        assertEquals(entity.name, entity2.name)
    }

    @Test
    fun `community mapping handles null userId with empty string`() {
        val c = Community(id = 1, title = "T", description = "D", image = null, userId = null)
        val dto = c.toDto()
        assertEquals("", dto.userId)
    }

    @Test
    fun `post toDto throws when communityId null`() {
        val p = Post(id = 1, content = "C", image = null, userId = "u", communityId = null)
        assertThrows(IllegalStateException::class.java) { p.toDto() }
    }

    @Test
    fun `comment mapping roundtrip`() {
        val dto = CommentDto(id = null, content = "nice", userId = "u", postId = 2)
        val e = dto.toEntity()
        assertEquals("nice", e.content)
        assertEquals("u", e.userId)
        assertEquals(2, e.postId)

        val dto2 = e.toDto()
        assertEquals(dto.content, dto2.content)
        assertEquals(dto.userId, dto2.userId)
        assertEquals(dto.postId, dto2.postId)
    }
}

