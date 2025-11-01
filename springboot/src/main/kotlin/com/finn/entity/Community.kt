package com.finn.entity

import jakarta.persistence.*

@Entity
@Table(name = "communities")
data class Community(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,
    @Column(name = "title", nullable = false, unique = true, length = 25)
    var title: String,
    @Column(name = "description", nullable = false, length = 100)
    var description: String,
    @Column(name = "image")
    var image: String? = null,
    @Column(name = "user_id")
    var userId: String? = null,
)
