package com.memorizy.server.dto

// Данные карточки для клиента

data class CardDto (
    val id: Long? = null,
    val term: String,
    val definition: String,
    val studySetId: Long,
    val createdAt: Long? = null,
    val level: Int? = null,
    val nextReviewDate: Long? = null
)