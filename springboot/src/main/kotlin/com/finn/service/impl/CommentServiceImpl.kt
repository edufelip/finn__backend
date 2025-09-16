package com.finn.service.impl

import com.finn.dto.CommentDto
import com.finn.entity.Comment
import com.finn.exception.NotFoundException
import com.finn.repository.CommentRepository
import com.finn.service.CommentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.finn.mapper.toDto
import com.finn.mapper.toEntity

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository
) : CommentService {

    @Transactional(readOnly = true)
    override fun getByPost(postId: Long): List<CommentDto> =
        commentRepository.findAllByPostIdOrderByIdDesc(postId).map { it.toDto() }

    @Transactional(readOnly = true)
    override fun getByUser(userId: String): List<CommentDto> =
        commentRepository.findAllByUserIdOrderByIdDesc(userId).map { it.toDto() }

    @Transactional(readOnly = true)
    override fun getOne(id: Long): CommentDto {
        val c = commentRepository.findById(id).orElseThrow { NotFoundException("Comment not found") }
        return c.toDto()
    }

    @Transactional
    override fun create(dto: CommentDto): CommentDto {
        val saved = commentRepository.save(dto.toEntity())
        return saved.toDto()
    }

    @Transactional
    override fun update(id: Long, dto: CommentDto) {
        val c = commentRepository.findById(id).orElseThrow { NotFoundException("Comment not found") }
        c.content = dto.content
        commentRepository.save(c)
    }

    @Transactional
    override fun delete(id: Long) {
        commentRepository.deleteById(id)
    }

    private fun Comment.toDto() = CommentDto(id = id, content = content, userId = userId, postId = postId)
}
