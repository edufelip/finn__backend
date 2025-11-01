package com.finn.mapper

import com.finn.dto.*
import com.finn.entity.*

// Entity -> DTO
fun User.toDto() = UserDto(id = this.id, name = this.name, photo = this.photo)

fun Community.toDto() =
    CommunityDto(
        id = this.id,
        title = this.title,
        description = this.description,
        image = this.image,
        userId = this.userId ?: "",
    )

fun Post.toDto() =
    PostDto(
        id = this.id,
        content = this.content,
        image = this.image,
        userId = this.userId ?: "",
        communityId = this.communityId ?: throw IllegalStateException("Post.communityId must not be null"),
    )

fun Comment.toDto() =
    CommentDto(
        id = this.id,
        content = this.content,
        userId = this.userId ?: "",
        postId = this.postId ?: throw IllegalStateException("Comment.postId must not be null"),
    )

fun Like.toDto() = LikeDto(id = this.id, userId = this.userId ?: "", postId = this.postId ?: 0L)

fun UserCommunity.toDto() = UserCommunityDto(id = this.id, userId = this.userId ?: "", communityId = this.communityId ?: 0L)

// DTO -> Entity (for create/update use-cases)
fun UserDto.toEntity() = User(id = this.id, name = this.name, photo = this.photo)

fun CommunityDto.toEntity() =
    Community(
        id = this.id,
        title = this.title,
        description = this.description,
        image = this.image,
        userId = this.userId,
    )

fun PostDto.toEntity() =
    Post(
        id = this.id,
        content = this.content,
        image = this.image,
        userId = this.userId,
        communityId = this.communityId,
    )

fun CommentDto.toEntity() =
    Comment(
        id = this.id,
        content = this.content,
        userId = this.userId,
        postId = this.postId,
    )
