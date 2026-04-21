package com.memorizy.server.controller

import com.memorizy.server.dto.CardDto
import com.memorizy.server.dto.StudySetDto
import com.memorizy.server.service.CardService
import com.memorizy.server.service.StudySetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// Endpoints для операций над наборами

@RestController
@RequestMapping("/api/sets")
class StudySetController(
    private val studySetService: StudySetService,
    private val cardService: CardService
) {

    @GetMapping
    fun getAllSets(): ResponseEntity<List<StudySetDto>> {
        return ResponseEntity.ok(studySetService.getAllStudySets())
    }

    @PostMapping
    fun createSet(@RequestBody dto: StudySetDto): ResponseEntity<StudySetDto> {
        return ResponseEntity.ok(studySetService.createStudySet(dto))
    }

    @PutMapping("/{id}")
    fun updateSet(@PathVariable id: Long, @RequestBody dto: StudySetDto): ResponseEntity<StudySetDto> {
        return ResponseEntity.ok(studySetService.updateStudySet(id, dto))
    }

    @DeleteMapping("/{id}")
    fun deleteSet(@PathVariable id: Long): ResponseEntity<Void> {
        studySetService.deleteStudySet(id)
        return ResponseEntity.noContent().build()   // Возвращаем 204 No Content
    }

    @GetMapping("/{setId}/cards")
    fun getCardsBySet(@PathVariable setId: Long): ResponseEntity<List<CardDto>> {
        return ResponseEntity.ok(cardService.getCardsBySetId(setId))
    }
}