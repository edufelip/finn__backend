package com.finn.service

import com.finn.dto.UserDto
import com.finn.entity.User
import com.finn.exception.ConflictException
import com.finn.exception.NotFoundException
import com.finn.repository.UserRepository
import com.finn.service.impl.UserServiceImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.Optional

class UserServiceImplTest {
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val service = UserServiceImpl(userRepository)

    @Test
    fun `getUser returns dto when found`() {
        val user = User(id = "user-1", name = "Alice")
        every { userRepository.findById("user-1") } returns Optional.of(user)

        val result = service.getUser("user-1")

        assertEquals("user-1", result.id)
        assertEquals("Alice", result.name)
    }

    @Test
    fun `getUser throws when not found`() {
        every { userRepository.findById("missing") } returns Optional.empty()

        assertThrows(NotFoundException::class.java) { service.getUser("missing") }
    }

    @Test
    fun `createUser persists when new`() {
        val dto = UserDto(id = "user-2", name = "Bob")
        every { userRepository.existsById(dto.id) } returns false
        val savedUser = slot<User>()
        every { userRepository.save(capture(savedUser)) } answers { savedUser.captured }

        val created = service.createUser(dto)

        assertEquals("user-2", created.id)
        verify { userRepository.save(match { it.id == "user-2" && it.name == "Bob" }) }
    }

    @Test
    fun `createUser throws when id exists`() {
        val dto = UserDto(id = "user-3", name = "Carol")
        every { userRepository.existsById(dto.id) } returns true

        assertThrows(ConflictException::class.java) { service.createUser(dto) }
    }

    @Test
    fun `updateUser persists new name`() {
        val entity = User(id = "user-4", name = "Old")
        every { userRepository.findById(entity.id) } returns Optional.of(entity)
        every { userRepository.save(any<User>()) } answers { it.invocation.args[0] as User }

        service.updateUser(entity.id, "New")

        verify { userRepository.save(match { it.id == "user-4" && it.name == "New" }) }
    }

    @Test
    fun `updateUser throws when missing`() {
        every { userRepository.findById("unknown") } returns Optional.empty()

        assertThrows(NotFoundException::class.java) { service.updateUser("unknown", "Name") }
    }

    @Test
    fun `deleteUser delegates to repository`() {
        service.deleteUser("user-5")

        verify { userRepository.deleteById("user-5") }
    }
}
