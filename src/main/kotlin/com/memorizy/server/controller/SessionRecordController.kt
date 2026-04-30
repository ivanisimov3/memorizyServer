package com.memorizy.server.controller

import com.memorizy.server.dto.SessionRecordDto
import com.memorizy.server.service.SessionRecordService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// Endpoints для операций над записями учебных сессий

@RestController
@RequestMapping("/api/sessions")
class SessionRecordController(
    private val sessionRecordService: SessionRecordService
) {

    @PostMapping
    fun createSessionRecord(@RequestBody dto: SessionRecordDto): ResponseEntity<SessionRecordDto> {
        return ResponseEntity.ok(sessionRecordService.createSessionRecord(dto))
    }
}