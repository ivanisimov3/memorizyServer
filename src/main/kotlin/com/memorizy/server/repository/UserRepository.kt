package com.memorizy.server.repository

import com.memorizy.server.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

// Репозиторий для операций с сущностью User в БД
// https://docs.spring.io/spring-data/rest/reference/data-commons/repositories/query-methods-details.html
// https://docs.spring.io/spring-data/rest/reference/data-commons/repositories/query-keywords-reference.html#appendix.query.method.subject

// JpaRepository<Сущность, Тип_ID>
interface UserRepository : JpaRepository<User, Long> {

    // SELECT * FROM users WHERE username = ?
    // Может не найтись (Optional)
    fun findByUsername(username: String): Optional<User>

    // SELECT count(*) > 0 FROM users WHERE username = ?
    // Проверка занят ли username
    fun existsByUsername(username: String): Boolean
}