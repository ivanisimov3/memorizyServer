package com.memorizy.server.repository

import com.memorizy.server.model.StudySet
import org.springframework.data.jpa.repository.JpaRepository

// Репозиторий для операций с сущностью StudySet в БД
// https://docs.spring.io/spring-data/rest/reference/data-commons/repositories/query-methods-details.html
// https://docs.spring.io/spring-data/rest/reference/data-commons/repositories/query-keywords-reference.html#appendix.query.method.subject

// JpaRepository<Сущность, Тип_ID>
interface StudySetRepository : JpaRepository<StudySet, Long> {

    // SELECT * FROM study_sets JOIN users ON study_sets.user_id = users.id WHERE users.username = ?
    // Найти все по полю User (StudySet model), а у юзера по полю Username (User model)
    fun findAllByUserUsername(username: String): List<StudySet>
}