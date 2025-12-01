package com.memorizy.server.dto

data class StudySetDto(
    val id: Int? = null, // ID может не быть при создании
    val name: String,
    val description: String?,
    val iconId: Int
)