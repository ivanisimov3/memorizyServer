package com.memorizy.server.model

import jakarta.persistence.*

// The entity for which Hibernate will create the tables itself

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assign database identity column
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    val passwordHash: String
)