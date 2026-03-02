package com.memorizy.server.dto

// Данные набора для клиента

data class StudySetDto(
    val id: Long? = null,
    val name: String,
    val description: String?,
    val iconId: Int,
    val createdAt: Long? = null,
    val targetDate: Long? = null
)