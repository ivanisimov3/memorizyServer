package com.memorizy.server.controller

import com.memorizy.server.dto.StudySetDto
import com.memorizy.server.service.StudySetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// Controller, the task is to accept the request, send it to the correct service, receive a response and send it back.

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
    fun deleteSet(@PathVariable id: Long): ResponseEntity<Void> {
        studySetService.deleteStudySet(id)
        return ResponseEntity.noContent().build()   // Возвращаем 204 No Content
    }
}