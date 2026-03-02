package com.memorizy.server.repository

import com.memorizy.server.model.Card
import org.springframework.data.jpa.repository.JpaRepository

// Репозиторий для операций с сущностью Card в БД
// https://docs.spring.io/spring-data/rest/reference/data-commons/repositories/query-methods-details.html
// https://docs.spring.io/spring-data/rest/reference/data-commons/repositories/query-keywords-reference.html#appendix.query.method.subject

// JpaRepository<Сущность, Тип_ID>
interface CardRepository : JpaRepository<Card, Long> {

    // SELECT * FROM cards WHERE study_set_id = ?
    // Найти все по полю StudySet (Card model), а у набора по полю id (StudySet model)
    fun findAllByStudySetId(studySetId: Long): List<Card>
}