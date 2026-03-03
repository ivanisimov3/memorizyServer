package com.memorizy.server.dto

// Параметры карточки для передачи данных

data class CardDto (
    val id: Long? = null,
    val term: String,
    val definition: String,
    val studySetId: Long,
    val createdAt: Long? = null,
    val level: Int = 0,
    val nextReviewDate: Long? = null
)