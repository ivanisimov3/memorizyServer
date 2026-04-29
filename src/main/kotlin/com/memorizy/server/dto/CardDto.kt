package com.memorizy.server.dto

// Параметры карточки для передачи данных

data class CardDto (
    val id: Long? = null,
    val term: String,
    val definition: String,
    val definitionVariants: List<String> = emptyList(),
    val studySetId: Long,
    val createdAt: Long? = null,
    val level: Int = 0,
    val nextReviewDate: Long? = null,
    val reviewCount: Int = 0,
    val mistakeCount: Int = 0,
    val recentAnswerHistory: String = ""
)