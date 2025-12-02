package com.memorizy.server.repository

import com.memorizy.server.model.Card
import org.springframework.data.jpa.repository.JpaRepository

interface CardRepository : JpaRepository<Card, Int> {

    // Найти все по полю StudySet (Card model), а у набора по полю id (StudySet model)
    fun findAllByStudySetId(studySetId: Int): List<Card>
}