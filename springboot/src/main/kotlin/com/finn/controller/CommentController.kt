package com.finn.controller

import com.finn.dto.CommentDto
import com.finn.service.CommentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/comments")
class CommentController(private val commentService: CommentService) {
    @GetMapping("/posts/{id}")
    fun byPost(
        @PathVariable id: Long,
    ): ResponseEntity<List<CommentDto>> = ResponseEntity.ok(commentService.getByPost(id))

    @GetMapping("/users/{id}")
    fun byUser(
        @PathVariable id: String,
    ): ResponseEntity<List<CommentDto>> = ResponseEntity.ok(commentService.getByUser(id))

    @GetMapping("/{id}")
    fun one(
        @PathVariable id: Long,
    ): ResponseEntity<CommentDto> = ResponseEntity.ok(commentService.getOne(id))

    @PostMapping
    fun create(
        @Valid @RequestBody dto: CommentDto,
    ): ResponseEntity<CommentDto> = ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(dto))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: CommentDto,
    ): ResponseEntity<Void> {
        commentService.update(id, dto)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        commentService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
