package com.memorizy.server.dto

// DTO (Data Transfer Object) - objects between client and server

data class CardDto (
    val id: Long? = null,
    val term: String,
    val definition: String,
    val studySetId: Long
)