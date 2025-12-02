package com.memorizy.server.dto

data class CardDto (
    val id: Int? = null,
    val term: String,
    val definition: String,
    val studySetId: Int
)