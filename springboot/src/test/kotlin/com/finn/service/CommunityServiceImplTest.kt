package com.finn.service

import com.finn.dto.CommunityDto
import com.finn.dto.UserCommunityDto
import com.finn.entity.Community
import com.finn.entity.UserCommunity
import com.finn.exception.ConflictException
import com.finn.exception.NotFoundException
import com.finn.repository.CommunityRepository
import com.finn.repository.UserCommunityRepository
import com.finn.service.impl.CommunityServiceImpl
import com.finn.storage.StorageService
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.web.multipart.MultipartFile
import java.util.Optional

class CommunityServiceImplTest {
    private val communityRepository: CommunityRepository = mockk(relaxed = true)
    private val userCommunityRepository: UserCommunityRepository = mockk(relaxed = true)
    private val storage: StorageService = mockk(relaxed = true)
    private val service = CommunityServiceImpl(communityRepository, userCommunityRepository, storage)

    @Test
    fun `list filters by search ignoring case`() {
        every { communityRepository.findAll() } returns
            listOf(
                Community(id = 1, title = "Kotlin", description = "Lang", userId = "u"),
                Community(id = 2, title = "Java", description = "Lang", userId = "u"),
            )

        val result = service.list("kot")

        assertEquals(1, result.size)
        assertEquals("Kotlin", result.first().title)
    }

    @Test
    fun `create throws when duplicate title`() {
        val dto = CommunityDto(id = null, title = "Dev", description = "Desc", image = null, userId = "u")
        every { communityRepository.existsByTitleIgnoreCase("Dev") } returns true

        assertThrows(ConflictException::class.java) { service.create(dto) }
    }

    @Test
    fun `updateImageWithStorage stores new file and deletes old`() {
        val file = mockk<MultipartFile>()
        val community = Community(id = 5, title = "Dev", description = "Desc", image = "old.png", userId = "u")
        every { communityRepository.findById(5) } returns Optional.of(community)
        every { storage.storePngWithChecks(file) } returns "new.png"
        every { communityRepository.save(any<Community>()) } returnsArgument 0

        service.updateImageWithStorage(5, file)

        verify { storage.deleteIfExists("old.png") }
        assertEquals("new.png", community.image)
    }

    @Test
    fun `subscribe returns existing link`() {
        val link = UserCommunity(id = 9, userId = "u", communityId = 4)
        every { communityRepository.findById(4) } returns Optional.of(Community(id = 4, title = "Dev", description = "d", userId = "u"))
        every { userCommunityRepository.findByUserIdAndCommunityId("u", 4) } returns link

        val dto = service.subscribe("u", 4)

        assertEquals(UserCommunityDto(id = 9, userId = "u", communityId = 4), dto)
        verify(exactly = 0) { userCommunityRepository.save(any()) }
    }

    @Test
    fun `subscribe persists when new`() {
        val slot: CapturingSlot<UserCommunity> = slot()
        every { communityRepository.findById(4) } returns Optional.of(Community(id = 4, title = "Dev", description = "d", userId = "owner"))
        every { userCommunityRepository.findByUserIdAndCommunityId("u", 4) } returns null
        every { userCommunityRepository.save(capture(slot)) } answers { slot.captured.copy(id = 11) }

        val dto = service.subscribe("u", 4)

        assertEquals(11, dto.id)
        assertEquals("u", dto.userId)
        assertEquals(4, dto.communityId)
    }

    @Test
    fun `subscribe throws when community missing`() {
        every { communityRepository.findById(10) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) { service.subscribe("u", 10) }
    }

    @Test
    fun `subscribersCount validates existence`() {
        every { communityRepository.findById(10) } returns Optional.of(Community(id = 10, title = "Dev", description = "d", userId = "o"))
        every { userCommunityRepository.countByCommunityId(10) } returns 5

        assertEquals(5, service.subscribersCount(10))
    }

    @Test
    fun `unsubscribe deletes link when present`() {
        val link = UserCommunity(id = 3, userId = "u", communityId = 4)
        every { userCommunityRepository.findByUserIdAndCommunityId("u", 4) } returns link

        service.unsubscribe("u", 4)

        verify { userCommunityRepository.delete(link) }
    }

    @Test
    fun `getOne throws when missing`() {
        every { communityRepository.findById(55) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) { service.getOne(55) }
    }

    @Test
    fun `update writes new fields`() {
        val entity = Community(id = 7, title = "Old", description = "Desc", image = null, userId = "u")
        every { communityRepository.findById(7) } returns Optional.of(entity)
        every { communityRepository.save(any<Community>()) } returnsArgument 0

        service.update(
            7,
            CommunityDto(
                id = 7,
                title = "New",
                description = "New desc",
                image = "img.png",
                userId = "other",
            ),
        )

        assertEquals("New", entity.title)
        assertEquals("img.png", entity.image)
    }

    @Test
    fun `listByUser maps related communities`() {
        every { userCommunityRepository.findAllByUserId("u") } returns
            listOf(UserCommunity(id = 1, userId = "u", communityId = 7))
        every { communityRepository.findAllById(setOf(7L)) } returns
            listOf(Community(id = 7, title = "Dev", description = "d", userId = "o"))

        val result = service.listByUser("u")

        assertEquals(1, result.size)
        assertEquals(7, result.first().id)
    }

    @Test
    fun `subscription proxies repository`() {
        val link = UserCommunity(id = 1, userId = "u", communityId = 3)
        every { userCommunityRepository.findByUserIdAndCommunityId("u", 3) } returns link

        val result = service.subscription("u", 3)

        assertSame(link, result)
    }

    @Test
    fun `delete delegates to repository`() {
        service.delete(9)

        verify { communityRepository.deleteById(9) }
    }
}
