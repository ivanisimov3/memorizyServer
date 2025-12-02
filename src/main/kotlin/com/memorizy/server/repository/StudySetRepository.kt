package com.memorizy.server.repository

import com.memorizy.server.model.StudySet
import org.springframework.data.jpa.repository.JpaRepository

interface StudySetRepository : JpaRepository<StudySet, Int> {

    // Найти все по полю User (StudySet model), а у юзера по полю Username (User model)
    fun findAllByUserUsername(username: String): List<StudySet>
}