package com.finn.service

import com.finn.entity.Community
import com.finn.repository.CommunityRepository
import com.finn.repository.UserCommunityRepository
import com.finn.service.impl.CommunityServiceImpl
import com.finn.storage.StorageService
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import java.util.*

class CommunityServiceImageUpdateTest {

    @Test
    fun `updateImageWithStorage stores new image updates db and deletes old`() {
        val communityRepository = mockk<CommunityRepository>(relaxed = true)
        val userCommunityRepository = mockk<UserCommunityRepository>(relaxed = true)
        val storage = mockk<StorageService>(relaxed = true)

        val comm = Community(id = 10, title = "T", description = "D", image = "old.png", userId = "u")
        every { communityRepository.findById(10) } returns Optional.of(comm)
        every { storage.storePngWithChecks(any()) } returns "new.png"

        val service = CommunityServiceImpl(communityRepository, userCommunityRepository, storage)
        val file = MockMultipartFile("community", "a.png", "image/png", ByteArray(10))
        service.updateImageWithStorage(10, file)

        val savedSlot: CapturingSlot<Community> = slot()
        verify { communityRepository.save(capture(savedSlot)) }
        assert(savedSlot.captured.image == "new.png")
        verify { storage.deleteIfExists("old.png") }
    }
}

