package com.memorizy.server.service

import com.memorizy.server.dto.StudySetDto
import com.memorizy.server.model.StudySet
import com.memorizy.server.model.User
import com.memorizy.server.repository.StudySetRepository
import com.memorizy.server.repository.UserRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

class StudySetServiceTest {

    private val studySetRepository = mockk<StudySetRepository>()
    private val userRepository = mockk<UserRepository>()

    private val service = StudySetService(
        studySetRepository = studySetRepository,
        userRepository = userRepository
    )

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `createStudySet creates set for current user`() {
        val owner = user(id = 1, username = "owner")
        val request = StudySetDto(
            name = "Биология",
            description = "Клетка",
            iconId = 4,
            targetDate = 1_700_000_000_000
        )

        authenticateAs(owner.username)
        every { userRepository.findByUsername(owner.username) } returns Optional.of(owner)
        every { studySetRepository.save(any()) } answers {
            firstArg<StudySet>().copy(id = 25, createdAt = 123_456L)
        }

        val response = service.createStudySet(request)

        assertEquals(25L, response.id)
        assertEquals(123_456L, response.createdAt)
        assertEquals(request.name, response.name)
    }

    @Test
    fun `getAllStudySets returns only current user sets`() {
        val owner = user(id = 1, username = "owner")
        val ownerSets = listOf(
            studySet(id = 1, name = "История", user = owner),
            studySet(id = 2, name = "Физика", user = owner)
        )

        authenticateAs(owner.username)
        every { userRepository.findByUsername(owner.username) } returns Optional.of(owner)
        every { studySetRepository.findAllByUserUsername(owner.username) } returns ownerSets

        val result = service.getAllStudySets()

        assertEquals(2, result.size)
        assertEquals(listOf("История", "Физика"), result.map { it.name })
    }

    @Test
    fun `updateStudySet updates own set`() {
        val owner = user(id = 1, username = "owner")
        val existing = studySet(id = 5, name = "Старое имя", description = "old", user = owner)
        val request = StudySetDto(
            name = "Новое имя",
            description = "new",
            iconId = 9,
            targetDate = 999L
        )

        authenticateAs(owner.username)
        every { studySetRepository.findById(existing.id) } returns Optional.of(existing)
        every { userRepository.findByUsername(owner.username) } returns Optional.of(owner)
        every { studySetRepository.save(any()) } answers { firstArg() }

        val result = service.updateStudySet(existing.id, request)

        assertEquals(existing.id, result.id)
        assertEquals(request.name, result.name)
        assertEquals(request.targetDate, result.targetDate)
    }

    @Test
    fun `updateStudySet returns 403 for foreign set`() {
        val owner = user(id = 1, username = "owner")
        val anotherUser = user(id = 2, username = "another")
        val foreignSet = studySet(id = 8, name = "Чужой", user = anotherUser)

        authenticateAs(owner.username)
        every { studySetRepository.findById(foreignSet.id) } returns Optional.of(foreignSet)
        every { userRepository.findByUsername(owner.username) } returns Optional.of(owner)

        val ex = assertThrows<ResponseStatusException> {
            service.updateStudySet(8, StudySetDto(name = "x", description = null, iconId = 1))
        }

        assertEquals(HttpStatus.FORBIDDEN, ex.statusCode)
    }

    @Test
    fun `updateStudySet returns 404 when set not found`() {
        authenticateAs("owner")
        every { studySetRepository.findById(99) } returns Optional.empty()

        val ex = assertThrows<ResponseStatusException> {
            service.updateStudySet(99, StudySetDto(name = "x", description = null, iconId = 1))
        }

        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)
    }

    @Test
    fun `deleteStudySet deletes own set`() {
        val owner = user(id = 1, username = "owner")
        val set = studySet(id = 10, name = "Удаляемый", user = owner)

        authenticateAs(owner.username)
        every { studySetRepository.findById(set.id) } returns Optional.of(set)
        every { userRepository.findByUsername(owner.username) } returns Optional.of(owner)
        every { studySetRepository.delete(set) } just runs

        service.deleteStudySet(set.id)

        verify(exactly = 1) { studySetRepository.delete(set) }
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
        name: String,
        user: User,
        description: String? = null
    ) = StudySet(
        id = id,
        name = name,
        description = description,
        iconId = 1,
        createdAt = 10L,
        user = user
    )
}