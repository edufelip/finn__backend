package com.finn.entity

import jakarta.persistence.*

@Entity
@Table(name = "comments")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,
    @Column(name = "content", nullable = false, length = 200)
    var content: String,
    @Column(name = "user_id")
    var userId: String? = null,
    @Column(name = "post_id")
    var postId: Long? = null,
)
