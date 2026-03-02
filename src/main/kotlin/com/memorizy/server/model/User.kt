package com.memorizy.server.model

import jakarta.persistence.*

// Сущность пользователь

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // The persistence provider должен назначить первичные ключи для объекта, используя столбец id.
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    val passwordHash: String
)