package com.memorizy.server.controller

import com.memorizy.server.dto.StudySetDto
import com.memorizy.server.service.StudySetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sets")
class StudySetController(
    private val studySetService: StudySetService
) {

    @GetMapping
    fun getAllSets(): ResponseEntity<List<StudySetDto>> {
        return ResponseEntity.ok(studySetService.getAllStudySets())
    }

    @PostMapping
    fun createSet(@RequestBody dto: StudySetDto): ResponseEntity<StudySetDto> {
        return ResponseEntity.ok(studySetService.createStudySet(dto))
    }

    @DeleteMapping("/{id}")
    fun deleteSet(@PathVariable id: Int): ResponseEntity<Void> {
        studySetService.deleteStudySet(id)
        return ResponseEntity.noContent().build() // Возвращаем 204 No Content
    }
}