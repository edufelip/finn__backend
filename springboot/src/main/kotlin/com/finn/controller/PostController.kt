package com.finn.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.finn.dto.PostDto
import com.finn.service.PostService
import com.finn.storage.StorageService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService,
    private val storage: StorageService,
    private val objectMapper: ObjectMapper,
) {
    @GetMapping("/users/{id}/feed")
    fun feed(
        @PathVariable id: String,
        @RequestParam(required = false, defaultValue = "1") page: Int,
    ): ResponseEntity<List<PostDto>> = ResponseEntity.ok(postService.getFeed(id, page))

    @GetMapping("/users/{id}")
    fun byUser(
        @PathVariable id: String,
        @RequestParam(required = false, defaultValue = "1") page: Int,
    ): ResponseEntity<List<PostDto>> = ResponseEntity.ok(postService.getByUser(id, page))

    @GetMapping("/communities/{id}")
    fun byCommunity(
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "1") page: Int,
    ): ResponseEntity<List<PostDto>> = ResponseEntity.ok(postService.getByCommunity(id, page))

    @GetMapping("/{id}")
    fun one(
        @PathVariable id: Long,
    ): ResponseEntity<PostDto> = ResponseEntity.ok(postService.getOne(id))

    @PostMapping
    fun create(
        @Valid @RequestBody dto: PostDto,
    ): ResponseEntity<PostDto> = ResponseEntity.status(HttpStatus.CREATED).body(postService.create(dto))

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createMultipart(
        request: HttpServletRequest,
        @RequestPart("post", required = false) file: MultipartFile?,
    ): ResponseEntity<PostDto> {
        val payload = request.getParameter("post") ?: throw IllegalArgumentException("missing post payload")
        val baseDto: PostDto = objectMapper.readValue(payload, PostDto::class.java)
        val filename = file?.let { storage.storePngWithChecks(it) }
        val dto = baseDto.copy(image = filename)
        val created = postService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: PostDto,
    ): ResponseEntity<Void> {
        postService.update(id, dto)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        postService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/likes")
    fun likeCount(
        @PathVariable id: Long,
    ): ResponseEntity<Int> = ResponseEntity.ok(postService.likeCount(id))

    @GetMapping("/{postId}/users/{userId}")
    fun hasLike(
        @PathVariable postId: Long,
        @PathVariable userId: String,
    ): ResponseEntity<Int> = ResponseEntity.ok(if (postService.hasLike(userId, postId)) 1 else 0)

    @PostMapping("/likes")
    fun like(
        @RequestParam("user_id") userId: String,
        @RequestParam("post_id") postId: Long,
    ) = ResponseEntity.status(HttpStatus.CREATED).body(postService.giveLike(userId, postId))

    @PostMapping("/likes/{id}")
    fun unlike(
        @PathVariable id: Long,
        @RequestBody body: Map<String, String>,
    ): ResponseEntity<Void> {
        val userId = body["id"] ?: return ResponseEntity.badRequest().build()
        postService.removeLike(userId, id)
        return ResponseEntity.noContent().build()
    }
}
