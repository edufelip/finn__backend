package com.finn.controller

import com.finn.dto.CommunityDto
import com.finn.service.CommunityService
import com.fasterxml.jackson.databind.ObjectMapper
import com.finn.storage.StorageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import jakarta.servlet.http.HttpServletRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.web.bind.annotation.RequestPart

@RestController
@RequestMapping("/communities")
class CommunityController(
    private val communityService: CommunityService,
    private val storage: StorageService,
    private val objectMapper: ObjectMapper
) {

    @GetMapping
    fun list(@RequestParam(required = false) search: String?): ResponseEntity<List<CommunityDto>> =
        ResponseEntity.ok(communityService.list(search))

    @GetMapping("/users/{id}")
    fun byUser(@PathVariable id: String): ResponseEntity<List<CommunityDto>> =
        ResponseEntity.ok(communityService.listByUser(id))

    @GetMapping("/{id}")
    fun one(@PathVariable id: Long): ResponseEntity<CommunityDto> = ResponseEntity.ok(communityService.getOne(id))

    @GetMapping("/{id}/subscribers")
    fun subscribers(@PathVariable id: Long): ResponseEntity<Int> = ResponseEntity.ok(communityService.subscribersCount(id))

    @GetMapping("/{communityId}/users/{userId}")
    fun subscription(@PathVariable communityId: Long, @PathVariable userId: String): ResponseEntity<Any?> =
        ResponseEntity.ok(communityService.subscription(userId, communityId))

    @PostMapping
    fun create(@Valid @RequestBody dto: CommunityDto): ResponseEntity<CommunityDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(communityService.create(dto))

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createMultipart(
        request: HttpServletRequest,
        @RequestPart("community", required = false) file: MultipartFile?
    ): ResponseEntity<CommunityDto> {
        val payload = request.getParameter("community") ?: throw IllegalArgumentException("missing community payload")
        val baseDto: CommunityDto = objectMapper.readValue(payload, CommunityDto::class.java)
        val filename = file?.let { storage.storePngWithChecks(it) }
        val dto = baseDto.copy(image = filename)
        val created = communityService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: CommunityDto): ResponseEntity<Void> {
        communityService.update(id, dto)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}/image")
    fun updateImage(@PathVariable id: Long, @RequestParam image: String): ResponseEntity<Void> {
        communityService.updateImage(id, image)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateImageMultipart(
        @PathVariable id: Long,
        @RequestPart("community") file: MultipartFile,
    ): ResponseEntity<Void> {
        communityService.updateImageWithStorage(id, file)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/subscribe")
    fun subscribe(@RequestParam("user_id") userId: String, @RequestParam("community_id") communityId: Long) =
        ResponseEntity.status(HttpStatus.CREATED).body(communityService.subscribe(userId, communityId))

    @PostMapping("/unsubscribe")
    fun unsubscribe(@RequestParam("user_id") userId: String, @RequestParam("community_id") communityId: Long): ResponseEntity<Void> {
        communityService.unsubscribe(userId, communityId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        communityService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
