package com.memorizy.server.dto

// DTO (Data Transfer Object) - objects between client and server

data class StudySetDto(
    val id: Long? = null,   // ID может не быть при создании
    val name: String,
    val description: String?,
    val iconId: Int,
    val createdAt: Long? = null
)