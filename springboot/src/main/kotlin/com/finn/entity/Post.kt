package com.finn.entity

import jakarta.persistence.*

@Entity
@Table(name = "posts")
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "image")
    var image: String? = null,

    @Column(name = "user_id")
    var userId: String? = null,

    @Column(name = "community_id")
    var communityId: Long? = null
)

