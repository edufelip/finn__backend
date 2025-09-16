package com.finn.entity

import jakarta.persistence.*

@Entity
@Table(name = "userscommunities")
data class UserCommunity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id")
    var userId: String? = null,

    @Column(name = "community_id")
    var communityId: Long? = null
)

