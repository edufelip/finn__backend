package com.finn.service

import com.finn.dto.*

interface UserService {
    fun getUser(id: String): UserDto

    fun createUser(dto: UserDto): UserDto

    fun updateUser(
        id: String,
        name: String,
    )

    fun deleteUser(id: String)
}

interface PostService {
    fun getFeed(
        userId: String,
        page: Int = 1,
    ): List<PostDto>

    fun getByUser(
        userId: String,
        page: Int = 1,
    ): List<PostDto>

    fun getByCommunity(
        communityId: Long,
        page: Int = 1,
    ): List<PostDto>

    fun getOne(id: Long): PostDto

    fun create(dto: PostDto): PostDto

    fun update(
        id: Long,
        dto: PostDto,
    )

    fun delete(id: Long)

    fun likeCount(postId: Long): Int

    fun giveLike(
        userId: String,
        postId: Long,
    ): com.finn.dto.LikeDto

    fun removeLike(
        userId: String,
        postId: Long,
    )

    fun hasLike(
        userId: String,
        postId: Long,
    ): Boolean
}

interface CommunityService {
    fun list(search: String?): List<CommunityDto>

    fun listByUser(userId: String): List<CommunityDto>

    fun getOne(id: Long): CommunityDto

    fun create(dto: CommunityDto): CommunityDto

    fun update(
        id: Long,
        dto: CommunityDto,
    )

    fun updateImage(
        id: Long,
        image: String,
    )

    fun updateImageWithStorage(
        id: Long,
        file: org.springframework.web.multipart.MultipartFile,
    )

    fun delete(id: Long)

    fun subscribe(
        userId: String,
        communityId: Long,
    ): com.finn.dto.UserCommunityDto

    fun unsubscribe(
        userId: String,
        communityId: Long,
    )

    fun subscription(
        userId: String,
        communityId: Long,
    ): Any?

    fun subscribersCount(id: Long): Int
}

interface CommentService {
    fun getByPost(postId: Long): List<CommentDto>

    fun getByUser(userId: String): List<CommentDto>

    fun getOne(id: Long): CommentDto

    fun create(dto: CommentDto): CommentDto

    fun update(
        id: Long,
        dto: CommentDto,
    )

    fun delete(id: Long)
}
