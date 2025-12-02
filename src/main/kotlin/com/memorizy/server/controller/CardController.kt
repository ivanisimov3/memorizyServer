package com.memorizy.server.controller

import com.memorizy.server.dto.CardDto
import com.memorizy.server.service.CardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cards")
class CardController(
    private val cardService: CardService
) {

    // Добавить карточку
    @PostMapping
    fun createCard(@RequestBody dto: CardDto): ResponseEntity<CardDto> {
        return ResponseEntity.ok(cardService.createCard(dto))
    }

    // Получить карточки конкретного набора
    // Пример: GET /api/cards/by-set/5
    @GetMapping("/by-set/{setId}")
    fun getCardsBySet(@PathVariable setId: Int): ResponseEntity<List<CardDto>> {
        return ResponseEntity.ok(cardService.getCardsBySetId(setId))
    }

    // Удалить карточку
    // Пример: DELETE /api/cards/10
    @DeleteMapping("/{id}")
    fun deleteCard(@PathVariable id: Int): ResponseEntity<Void> {
        cardService.deleteCard(id)
        return ResponseEntity.noContent().build()
    }
}