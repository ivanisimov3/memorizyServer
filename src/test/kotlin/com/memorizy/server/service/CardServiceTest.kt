package com.memorizy.server.service

import com.memorizy.server.dto.CardDto
import com.memorizy.server.model.Card
import com.memorizy.server.model.StudySet
import com.memorizy.server.model.User
import com.memorizy.server.repository.CardRepository
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

class CardServiceTest {

    private val cardRepository = mockk<CardRepository>()
    private val studySetRepository = mockk<StudySetRepository>()

    private val service = CardService(
        cardRepository = cardRepository,
        studySetRepository = studySetRepository
    )

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `createCard creates card in own set and normalizes definitionVariants`() {
        val owner = user(id = 1, username = "owner")
        val set = studySet(id = 100, user = owner)
        val request = CardDto(
            term = "Клетка",
            definition = "Базовая единица",
            definitionVariants = listOf("  ", "Базовая единица", "Структурная единица", "Структурная единица"),
            studySetId = 100,
            level = 2,
            nextReviewDate = 555L
        )
        val capturedCard = slot<Card>()

        authenticateAs(owner.username)
        every { studySetRepository.findById(request.studySetId) } returns Optional.of(set)
        every { cardRepository.save(capture(capturedCard)) } answers {
            capturedCard.captured.copy(id = 50)
        }

        val response = service.createCard(request)

        assertEquals(50L, response.id)
        assertEquals(listOf("Структурная единица"), response.definitionVariants)
        assertEquals("[\"Структурная единица\"]", capturedCard.captured.definitionVariantsJson)
    }

    @Test
    fun `createCard returns 404 when set not found`() {
        authenticateAs("owner")
        every { studySetRepository.findById(100) } returns Optional.empty()

        val ex = assertThrows<ResponseStatusException> {
            service.createCard(
                CardDto(term = "Клетка", definition = "Определение", studySetId = 100)
            )
        }

        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)
    }

    @Test
    fun `createCard returns 403 for foreign set`() {
        val anotherUser = user(id = 2, username = "another")
        val foreignSet = studySet(id = 100, user = anotherUser)

        authenticateAs("owner")
        every { studySetRepository.findById(foreignSet.id) } returns Optional.of(foreignSet)

        val ex = assertThrows<ResponseStatusException> {
            service.createCard(
                CardDto(term = "Клетка", definition = "Определение", studySetId = 100)
            )
        }

        assertEquals(HttpStatus.FORBIDDEN, ex.statusCode)
    }

    @Test
    fun `getCardsBySetId returns empty definitionVariants for corrupted JSON`() {
        val owner = user(id = 1, username = "owner")
        val set = studySet(id = 100, user = owner)
        val brokenCard = card(
            id = 10,
            term = "Клетка",
            definition = "Определение",
            definitionVariantsJson = "not-a-json",
            studySet = set
        )

        authenticateAs(owner.username)
        every { studySetRepository.findById(set.id) } returns Optional.of(set)
        every { cardRepository.findAllByStudySetId(set.id) } returns listOf(brokenCard)

        val result = service.getCardsBySetId(set.id)

        assertEquals(1, result.size)
        assertEquals(emptyList<String>(), result[0].definitionVariants)
    }

    @Test
    fun `updateCard updates own card`() {
        val owner = user(id = 1, username = "owner")
        val set = studySet(id = 100, user = owner)
        val existingCard = card(
            id = 10,
            term = "Старый термин",
            definition = "Старое определение",
            definitionVariantsJson = "[\"Старый вариант\"]",
            studySet = set
        )
        val request = CardDto(
            term = "Новый термин",
            definition = "Новое определение",
            definitionVariants = listOf("Новое определение", "Новый вариант"),
            studySetId = 100,
            level = 4,
            nextReviewDate = 777L
        )

        authenticateAs(owner.username)
        every { cardRepository.findById(existingCard.id) } returns Optional.of(existingCard)
        every { cardRepository.save(any()) } answers { firstArg() }

        val result = service.updateCard(existingCard.id, request)

        assertEquals(request.term, result.term)
        assertEquals(listOf("Новый вариант"), result.definitionVariants)
        assertEquals(request.nextReviewDate, result.nextReviewDate)
    }

    @Test
    fun `updateCard returns 403 for foreign card`() {
        val anotherUser = user(id = 2, username = "another")
        val foreignSet = studySet(id = 100, user = anotherUser)
        val foreignCard = card(id = 10, term = "T", definition = "D", studySet = foreignSet)

        authenticateAs("owner")
        every { cardRepository.findById(foreignCard.id) } returns Optional.of(foreignCard)

        val ex = assertThrows<ResponseStatusException> {
            service.updateCard(
                foreignCard.id,
                CardDto(term = "Новый", definition = "Новое", studySetId = 100)
            )
        }

        assertEquals(HttpStatus.FORBIDDEN, ex.statusCode)
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

    private fun card(
        id: Long,
        term: String,
        definition: String,
        definitionVariantsJson: String = "[]",
        studySet: StudySet
    ) = Card(
        id = id,
        term = term,
        definition = definition,
        definitionVariantsJson = definitionVariantsJson,
        createdAt = 11L,
        level = 1,
        nextReviewDate = 12L,
        studySet = studySet
    )
}