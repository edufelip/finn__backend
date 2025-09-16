package com.finn.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "photo")
    var photo: String? = null,

    @Column(name = "date")
    var date: OffsetDateTime? = null
)

