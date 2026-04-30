package com.memorizy.server.service

import com.memorizy.server.dto.SessionRecordDto
import com.memorizy.server.model.SessionRecord
import com.memorizy.server.model.StudySet
import com.memorizy.server.model.User
import com.memorizy.server.repository.SessionRecordRepository
import com.memorizy.server.repository.StudySetRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

class SessionRecordServiceTest {

    private val sessionRecordRepository = mockk<SessionRecordRepository>()
    private val studySetRepository = mockk<StudySetRepository>()

    private val service = SessionRecordService(
        sessionRecordRepository = sessionRecordRepository,
        studySetRepository = studySetRepository
    )

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `createSessionRecord creates record in own set`() {
        val owner = user(id = 1, username = "owner")
        val set = studySet(id = 100, user = owner)
        val request = SessionRecordDto(
            studySetId = set.id,
            type = "learning",
            correctCount = 8,
            totalCount = 10,
            percentage = 80f,
            timestamp = 123_456L
        )
        val capturedRecord = slot<SessionRecord>()

        authenticateAs(owner.username)
        every { studySetRepository.findById(request.studySetId) } returns Optional.of(set)
        every { sessionRecordRepository.save(capture(capturedRecord)) } answers {
            capturedRecord.captured.copy(id = 50)
        }

        val response = service.createSessionRecord(request)

        assertEquals(50L, response.id)
        assertEquals(set.id, response.studySetId)
        assertEquals(request.type, response.type)
        assertEquals(request.correctCount, response.correctCount)
        assertEquals(request.timestamp, response.timestamp)
        assertEquals(set, capturedRecord.captured.studySet)
    }

    @Test
    fun `createSessionRecord returns 403 for foreign set`() {
        val anotherUser = user(id = 2, username = "another")
        val foreignSet = studySet(id = 100, user = anotherUser)

        authenticateAs("owner")
        every { studySetRepository.findById(foreignSet.id) } returns Optional.of(foreignSet)

        val ex = assertThrows<ResponseStatusException> {
            service.createSessionRecord(
                SessionRecordDto(
                    studySetId = foreignSet.id,
                    type = "testing",
                    correctCount = 1,
                    totalCount = 2,
                    percentage = 50f
                )
            )
        }

        assertEquals(HttpStatus.FORBIDDEN, ex.statusCode)
    }

    @Test
    fun `getSessionRecordsBySetId returns own set records`() {
        val owner = user(id = 1, username = "owner")
        val set = studySet(id = 100, user = owner)
        val records = listOf(
            sessionRecord(id = 1, type = "learning", studySet = set),
            sessionRecord(id = 2, type = "testing", studySet = set)
        )

        authenticateAs(owner.username)
        every { studySetRepository.findById(set.id) } returns Optional.of(set)
        every { sessionRecordRepository.findAllByStudySetId(set.id) } returns records

        val result = service.getSessionRecordsBySetId(set.id)

        assertEquals(2, result.size)
        assertEquals(listOf("learning", "testing"), result.map { it.type })
        assertEquals(listOf(set.id, set.id), result.map { it.studySetId })
    }

    private fun authenticateAs(username: String) {
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(username, null, emptyList())
    }

    private fun user(
        id: Long,
        username: String
    ) = User(
        id = id,
        username = username,
        passwordHash = "hash"
    )

    private fun studySet(
        id: Long,
        user: User
    ) = StudySet(
        id = id,
        name = "Набор $id",
        description = null,
        iconId = 1,
        createdAt = 10L,
        user = user
    )

    private fun sessionRecord(
        id: Long,
        type: String,
        studySet: StudySet
    ) = SessionRecord(
        id = id,
        type = type,
        correctCount = 7,
        totalCount = 10,
        percentage = 70f,
        timestamp = 123L,
        studySet = studySet
    )
}