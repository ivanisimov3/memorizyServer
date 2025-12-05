package com.memorizy.server.service

import com.memorizy.server.dto.CardDto
import com.memorizy.server.model.Card
import com.memorizy.server.repository.CardRepository
import com.memorizy.server.repository.StudySetRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

// A service class that stores business logic

@Service
class CardService(
    private val cardRepository: CardRepository,
    private val studySetRepository: StudySetRepository
) {

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
            studySet = studySet
        )

        val savedCard = cardRepository.save(card)

        return dto.copy(id = savedCard.id)  // Возвращаем DTO с новым ID
    }

    // Получить все карточки текущего пользователя
    fun getCardsBySetId(setId: Long): List<CardDto> {
        val studySet = studySetRepository.findById(setId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found") }

        if (studySet.user.username != getCurrentUsername()) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this set")
        }

        return cardRepository.findAllByStudySetId(setId).map { card ->
            CardDto(
                id = card.id,
                term = card.term,
                definition = card.definition,
                studySetId = setId,
                createdAt = card.createdAt
            )
        }
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
}