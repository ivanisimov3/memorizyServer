package com.memorizy.server.repository

import com.memorizy.server.model.SessionRecord
import org.springframework.data.jpa.repository.JpaRepository

// Репозиторий для операций с сущностью SessionRecord в БД

interface SessionRecordRepository : JpaRepository<SessionRecord, Long> {

    // SELECT * FROM session_records WHERE study_set_id = ?
    // Найти все по полю StudySet (SessionRecord model), а у набора по полю id (StudySet model)
    fun findAllByStudySetId(studySetId: Long): List<SessionRecord>
}