package com.finn.service.impl

import com.finn.dto.PostDto
import com.finn.dto.LikeDto
import com.finn.entity.Like
import com.finn.entity.Post
import com.finn.exception.ConflictException
import com.finn.exception.NotFoundException
import com.finn.repository.LikeRepository
import com.finn.repository.PostRepository
import com.finn.repository.UserCommunityRepository
import com.finn.service.PostService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.finn.mapper.toDto
import com.finn.mapper.toEntity

private const val PAGE_SIZE = 20

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
    private val userCommunityRepository: UserCommunityRepository
) : PostService {

    @Transactional(readOnly = true)
    override fun getFeed(userId: String, page: Int): List<PostDto> {
        val links = userCommunityRepository.findAllByUserId(userId)
        val commIds = links.mapNotNull { it.communityId }
        if (commIds.isEmpty()) return emptyList()
        val pageable = PageRequest.of(if (page <= 0) 0 else page - 1, PAGE_SIZE)
        return postRepository.findAllByCommunityIdInOrderByIdDesc(commIds, pageable)
            .content
            .map { it.toDto() }
    }

    @Transactional(readOnly = true)
    override fun getByUser(userId: String, page: Int): List<PostDto> {
        val pageable = PageRequest.of(if (page <= 0) 0 else page - 1, PAGE_SIZE)
        return postRepository.findAllByUserIdOrderByIdDesc(userId, pageable).content.map { it.toDto() }
    }

    @Transactional(readOnly = true)
    override fun getByCommunity(communityId: Long, page: Int): List<PostDto> {
        val pageable = PageRequest.of(if (page <= 0) 0 else page - 1, PAGE_SIZE)
        return postRepository.findAllByCommunityIdOrderByIdDesc(communityId, pageable).content.map { it.toDto() }
    }

    @Transactional(readOnly = true)
    override fun getOne(id: Long): PostDto {
        val post = postRepository.findById(id).orElseThrow { NotFoundException("Post not found") }
        return post.toDto()
    }

    @Transactional
    override fun create(dto: PostDto): PostDto {
        val saved = postRepository.save(dto.toEntity())
        return saved.toDto()
    }

    @Transactional
    override fun update(id: Long, dto: PostDto) {
        val post = postRepository.findById(id).orElseThrow { NotFoundException("Post not found") }
        post.content = dto.content
        post.image = dto.image
        postRepository.save(post)
    }

    @Transactional
    override fun delete(id: Long) {
        postRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun likeCount(postId: Long): Int = likeRepository.countByPostId(postId)

    @Transactional
    override fun giveLike(userId: String, postId: Long): LikeDto {
        val existing = likeRepository.findByUserIdAndPostId(userId, postId)
        if (existing != null) throw ConflictException("Like already exists")
        val saved = likeRepository.save(Like(userId = userId, postId = postId))
        return LikeDto(id = saved.id, userId = userId, postId = postId)
    }

    @Transactional
    override fun removeLike(userId: String, postId: Long) {
        val existing = likeRepository.findByUserIdAndPostId(userId, postId)
        if (existing != null) likeRepository.delete(existing)
    }

    @Transactional(readOnly = true)
    override fun hasLike(userId: String, postId: Long): Boolean = likeRepository.findByUserIdAndPostId(userId, postId) != null

    private fun Post.toDto() = PostDto(id = id, content = content, image = image, userId = userId, communityId = communityId)
}
