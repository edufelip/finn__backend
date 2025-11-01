package com.finn.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UserDto(
    @field:NotBlank
    val id: String,
    @field:NotBlank
    val name: String,
    val photo: String? = null,
)

data class CommunityDto(
    val id: Long?,
    @field:NotBlank @field:Size(max = 25)
    val title: String,
    @field:NotBlank @field:Size(max = 100)
    val description: String,
    val image: String? = null,
    @field:NotBlank
    val userId: String,
)

data class PostDto(
    val id: Long?,
    @field:NotBlank
    val content: String,
    val image: String? = null,
    @field:NotBlank
    val userId: String,
    @field:NotNull
    val communityId: Long,
)

data class CommentDto(
    val id: Long?,
    @field:NotBlank @field:Size(max = 200)
    val content: String,
    @field:NotBlank
    val userId: String,
    @field:NotNull
    val postId: Long,
)

data class LikeDto(
    val id: Long?,
    val userId: String,
    val postId: Long,
)

data class UserCommunityDto(
    val id: Long?,
    val userId: String,
    val communityId: Long,
)
