package com.finn.service.impl

import com.finn.dto.CommunityDto
import com.finn.dto.UserCommunityDto
import com.finn.entity.Community
import com.finn.entity.UserCommunity
import com.finn.exception.ConflictException
import com.finn.exception.NotFoundException
import com.finn.repository.CommunityRepository
import com.finn.repository.UserCommunityRepository
import com.finn.service.CommunityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.finn.mapper.toDto
import com.finn.mapper.toEntity

@Service
class CommunityServiceImpl(
    private val communityRepository: CommunityRepository,
    private val userCommunityRepository: UserCommunityRepository,
    private val storage: com.finn.storage.StorageService
) : CommunityService {

    @Transactional(readOnly = true)
    override fun list(search: String?): List<CommunityDto> {
        val all = communityRepository.findAll()
        val filtered = if (search.isNullOrBlank()) all else all.filter { it.title.contains(search, ignoreCase = true) }
        return filtered.map { it.toDto() }
    }

    @Transactional(readOnly = true)
    override fun listByUser(userId: String): List<CommunityDto> {
        val links = userCommunityRepository.findAllByUserId(userId)
        val ids = links.mapNotNull { it.communityId }.toSet()
        val comms = communityRepository.findAllById(ids)
        return comms.map { it.toDto() }
    }

    @Transactional(readOnly = true)
    override fun getOne(id: Long): CommunityDto {
        val comm = communityRepository.findById(id).orElseThrow { NotFoundException("Community not found") }
        return comm.toDto()
    }

    @Transactional
    override fun create(dto: CommunityDto): CommunityDto {
        if (communityRepository.existsByTitleIgnoreCase(dto.title)) throw ConflictException("Community already exists")
        val saved = communityRepository.save(dto.toEntity())
        return saved.toDto()
    }

    @Transactional
    override fun update(id: Long, dto: CommunityDto) {
        val comm = communityRepository.findById(id).orElseThrow { NotFoundException("Community not found") }
        comm.title = dto.title
        comm.description = dto.description
        comm.image = dto.image
        comm.userId = dto.userId
        communityRepository.save(comm)
    }

    @Transactional
    override fun updateImage(id: Long, image: String) {
        val comm = communityRepository.findById(id).orElseThrow { NotFoundException("Community not found") }
        comm.image = image
        communityRepository.save(comm)
    }

    @Transactional
    override fun updateImageWithStorage(id: Long, file: org.springframework.web.multipart.MultipartFile) {
        val comm = communityRepository.findById(id).orElseThrow { NotFoundException("Community not found") }
        val old = comm.image
        val filename = storage.storePngWithChecks(file)
        comm.image = filename
        communityRepository.save(comm)
        storage.deleteIfExists(old)
    }

    @Transactional
    override fun delete(id: Long) {
        communityRepository.deleteById(id)
    }

    @Transactional
    override fun subscribe(userId: String, communityId: Long): UserCommunityDto {
        communityRepository.findById(communityId).orElseThrow { NotFoundException("Community not found") }
        val existing = userCommunityRepository.findByUserIdAndCommunityId(userId, communityId)
        if (existing != null) return UserCommunityDto(existing.id, existing.userId!!, existing.communityId!!)
        val saved = userCommunityRepository.save(UserCommunity(userId = userId, communityId = communityId))
        return UserCommunityDto(saved.id, userId, communityId)
    }

    @Transactional
    override fun unsubscribe(userId: String, communityId: Long) {
        val existing = userCommunityRepository.findByUserIdAndCommunityId(userId, communityId)
        if (existing != null) userCommunityRepository.delete(existing)
    }

    @Transactional(readOnly = true)
    override fun subscription(userId: String, communityId: Long): Any? =
        userCommunityRepository.findByUserIdAndCommunityId(userId, communityId)

    @Transactional(readOnly = true)
    override fun subscribersCount(id: Long): Int {
        communityRepository.findById(id).orElseThrow { NotFoundException("Community not found") }
        return userCommunityRepository.countByCommunityId(id)
    }

    private fun Community.toDto() = CommunityDto(id = id, title = title, description = description, image = image, userId = userId)
}
