package com.memorizy.server.repository

import com.memorizy.server.model.StudySet
import org.springframework.data.jpa.repository.JpaRepository

// Tools for working with study_sets table

// JpaRepository<Сущность, Тип_ID>
interface StudySetRepository : JpaRepository<StudySet, Long> {

    // SELECT * FROM study_sets JOIN users ON study_sets.user_id = users.id WHERE users.username = ?
    // Найти все по полю User (StudySet model), а у юзера по полю Username (User model)
    fun findAllByUserUsername(username: String): List<StudySet>
}