package com.memorizy.server.dto

// Параметры набора для передачи данных

data class StudySetDto(
    val id: Long? = null,
    val name: String,
    val description: String?,
    val iconId: Int,
    val createdAt: Long? = null,
    val targetDate: Long? = null
)