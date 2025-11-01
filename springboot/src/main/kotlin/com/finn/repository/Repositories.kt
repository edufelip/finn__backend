package com.finn.repository

import com.finn.entity.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String>

@Repository
interface CommunityRepository : JpaRepository<Community, Long> {
    fun existsByTitleIgnoreCase(title: String): Boolean
}

@Repository
interface PostRepository : JpaRepository<Post, Long> {
    fun findAllByUserIdOrderByIdDesc(userId: String): List<Post>

    fun findAllByCommunityIdOrderByIdDesc(communityId: Long): List<Post>

    fun findAllByUserIdOrderByIdDesc(
        userId: String,
        pageable: Pageable,
    ): Page<Post>

    fun findAllByCommunityIdOrderByIdDesc(
        communityId: Long,
        pageable: Pageable,
    ): Page<Post>

    fun findAllByCommunityIdInOrderByIdDesc(
        communityIds: Collection<Long>,
        pageable: Pageable,
    ): Page<Post>
}

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByPostIdOrderByIdDesc(postId: Long): List<Comment>

    fun findAllByUserIdOrderByIdDesc(userId: String): List<Comment>
}

@Repository
interface LikeRepository : JpaRepository<Like, Long> {
    fun countByPostId(postId: Long): Int

    fun findByUserIdAndPostId(
        userId: String,
        postId: Long,
    ): Like?
}

@Repository
interface UserCommunityRepository : JpaRepository<UserCommunity, Long> {
    fun findByUserIdAndCommunityId(
        userId: String,
        communityId: Long,
    ): UserCommunity?

    fun countByCommunityId(communityId: Long): Int

    fun findAllByUserId(userId: String): List<UserCommunity>
}
