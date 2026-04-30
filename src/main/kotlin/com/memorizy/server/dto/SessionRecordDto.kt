package com.memorizy.server.dto

// Параметры записи учебной сессии для передачи данных

data class SessionRecordDto(
    val id: Long? = null,
    val studySetId: Long,
    val type: String,
    val correctCount: Int,
    val totalCount: Int,
    val percentage: Float,
    val timestamp: Long? = null
)