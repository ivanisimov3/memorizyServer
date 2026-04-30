package com.memorizy.server.service

import com.memorizy.server.dto.SessionRecordDto
import com.memorizy.server.model.SessionRecord
import com.memorizy.server.repository.SessionRecordRepository
import com.memorizy.server.repository.StudySetRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

// Слой бизнес логики для операций над записями учебных сессий

@Service
class SessionRecordService(
    private val sessionRecordRepository: SessionRecordRepository,
    private val studySetRepository: StudySetRepository
) {

    // Найти текущий Username в базе
    private fun getCurrentUsername(): String {
        return SecurityContextHolder.getContext().authentication.name
    }

    // Создать запись учебной сессии у текущего пользователя
    fun createSessionRecord(dto: SessionRecordDto): SessionRecordDto {
        val studySet = studySetRepository.findById(dto.studySetId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found") }

        if (studySet.user.username != getCurrentUsername()) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this set")
        }

        val sessionRecord = SessionRecord(
            type = dto.type,
            correctCount = dto.correctCount,
            totalCount = dto.totalCount,
            percentage = dto.percentage,
            timestamp = dto.timestamp ?: System.currentTimeMillis(),
            studySet = studySet
        )

        val savedRecord = sessionRecordRepository.save(sessionRecord)

        return savedRecord.toDto()
    }

    // Получить все записи учебных сессий текущего пользователя
    fun getSessionRecordsBySetId(setId: Long): List<SessionRecordDto> {
        val studySet = studySetRepository.findById(setId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found") }

        if (studySet.user.username != getCurrentUsername()) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this set")
        }

        return sessionRecordRepository.findAllByStudySetId(setId).map { record ->
            record.toDto()
        }
    }

    private fun SessionRecord.toDto(): SessionRecordDto {
        return SessionRecordDto(
            id = id,
            studySetId = studySet.id,
            type = type,
            correctCount = correctCount,
            totalCount = totalCount,
            percentage = percentage,
            timestamp = timestamp
        )
    }
}