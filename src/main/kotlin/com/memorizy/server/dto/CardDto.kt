package com.memorizy.server.dto

// DTO (Data Transfer Object) - objects between client and server

data class CardDto (
    val id: Long? = null,   // ID может не быть при создании
    val term: String,
    val definition: String,
    val studySetId: Long,
    val createdAt: Long? = null
)