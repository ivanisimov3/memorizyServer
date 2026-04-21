package com.memorizy.server.controller

import com.memorizy.server.dto.CardDto
import com.memorizy.server.service.CardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// Endpoints для операций над карточками

@RestController
@RequestMapping("/api/cards")
class CardController(
    private val cardService: CardService
) {

    @PostMapping
    fun createCard(@RequestBody dto: CardDto): ResponseEntity<CardDto> {
        return ResponseEntity.ok(cardService.createCard(dto))
    }

    @PutMapping("/{id}")
    fun updateCard(@PathVariable id: Long, @RequestBody dto: CardDto): ResponseEntity<CardDto> {
        return ResponseEntity.ok(cardService.updateCard(id, dto))
    }

    @DeleteMapping("/{id}")
    fun deleteCard(@PathVariable id: Long): ResponseEntity<Void> {
        cardService.deleteCard(id)
        return ResponseEntity.noContent().build()   // Возвращаем 204 No Content
    }
}