package com.memorizy.server.repository

import com.memorizy.server.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

// JpaRepository<Сущность, Тип_ID>
interface UserRepository : JpaRepository<User, Long> {

    // SELECT * FROM users WHERE username = ?
    fun findByUsername(username: String): Optional<User>

    // Проверка существования (для регистрации)
    fun existsByUsername(username: String): Boolean
}