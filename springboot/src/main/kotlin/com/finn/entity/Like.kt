package com.finn.entity

import jakarta.persistence.*

@Entity
@Table(name = "likes")
data class Like(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id")
    var userId: String? = null,

    @Column(name = "post_id")
    var postId: Long? = null
)

