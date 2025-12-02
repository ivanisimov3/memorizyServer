package com.memorizy.server.repository

import com.memorizy.server.model.Card
import org.springframework.data.jpa.repository.JpaRepository

// Tools for working with cards table

// JpaRepository<Сущность, Тип_ID>
interface CardRepository : JpaRepository<Card, Long> {

    // SELECT * FROM cards WHERE study_set_id = ?
    // Найти все по полю StudySet (Card model), а у набора по полю id (StudySet model)
    fun findAllByStudySetId(studySetId: Long): List<Card>
}