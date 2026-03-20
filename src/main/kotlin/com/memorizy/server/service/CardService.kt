package com.memorizy.server.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.memorizy.server.dto.CardDto
import com.memorizy.server.model.Card
import com.memorizy.server.repository.CardRepository
import com.memorizy.server.repository.StudySetRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

// Слой бизнес логики для операций над карточками

@Service
class CardService(
    private val cardRepository: CardRepository,
    private val studySetRepository: StudySetRepository
) {

    private val objectMapper = jacksonObjectMapper()

    // Найти текущее Username в базе
    private fun getCurrentUsername(): String {
        return SecurityContextHolder.getContext().authentication.name
    }

    // Создать карточку у текущего пользователя
    fun createCard(dto: CardDto): CardDto {
        val studySet = studySetRepository.findById(dto.studySetId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found") }

        if (studySet.user.username != getCurrentUsername()) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this set")
        }

        val card = Card(
            term = dto.term,
            definition = dto.definition,
            definitionVariantsJson = serializeDefinitionVariants(
                primaryDefinition = dto.definition,
                rawVariants = dto.definitionVariants
            ),
            level = dto.level,
            nextReviewDate = dto.nextReviewDate ?: System.currentTimeMillis(),
            studySet = studySet
        )

        val savedCard = cardRepository.save(card)

        return savedCard.toDto()
    }

    // Получить все карточки текущего пользователя
    fun getCardsBySetId(setId: Long): List<CardDto> {
        val studySet = studySetRepository.findById(setId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found") }

        if (studySet.user.username != getCurrentUsername()) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this set")
        }

        return cardRepository.findAllByStudySetId(setId).map { card ->
            card.toDto()
        }
    }

    // Обновить карточку текущего пользователя
    fun updateCard(id: Long, dto: CardDto): CardDto {
        val existingCard = cardRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found") }

        if (existingCard.studySet.user.username != getCurrentUsername()) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this card")
        }

        val updatedCard = existingCard.copy(
            term = dto.term,
            definition = dto.definition,
            definitionVariantsJson = serializeDefinitionVariants(
                primaryDefinition = dto.definition,
                rawVariants = dto.definitionVariants
            ),
            level = dto.level,
            nextReviewDate = dto.nextReviewDate ?: existingCard.nextReviewDate
        )

        val savedCard = cardRepository.save(updatedCard)

        return savedCard.toDto()
    }

    // Удалить карточку
    fun deleteCard(cardId: Long) {
        val card = cardRepository.findById(cardId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found") }

        if (card.studySet.user.username != getCurrentUsername()) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this card")
        }

        cardRepository.delete(card)
    }

    private fun Card.toDto(): CardDto {
        return CardDto(
            id = id,
            term = term,
            definition = definition,
            definitionVariants = deserializeDefinitionVariants(
                primaryDefinition = definition,
                serializedVariants = definitionVariantsJson
            ),
            studySetId = studySet.id,
            createdAt = createdAt,
            level = level,
            nextReviewDate = nextReviewDate
        )
    }

    private fun serializeDefinitionVariants(
        primaryDefinition: String,
        rawVariants: List<String>
    ): String {
        val normalizedVariants = normalizeDefinitionVariants(primaryDefinition, rawVariants)
        return objectMapper.writeValueAsString(normalizedVariants)
    }

    private fun deserializeDefinitionVariants(
        primaryDefinition: String,
        serializedVariants: String
    ): List<String> {
        val rawVariants = runCatching {
            objectMapper.readValue<List<String>>(serializedVariants)
        }.getOrElse {
            emptyList()
        }

        return normalizeDefinitionVariants(primaryDefinition, rawVariants)
    }

    private fun normalizeDefinitionVariants(
        primaryDefinition: String,
        rawVariants: List<String>
    ): List<String> {
        val normalizedPrimary = primaryDefinition.trim()

        return rawVariants
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filter { it != normalizedPrimary }
            .distinct()
    }
}