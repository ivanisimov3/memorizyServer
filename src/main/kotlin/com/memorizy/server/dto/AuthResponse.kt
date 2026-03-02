package com.memorizy.server.dto

// Выходные данные при авторизации

data class AuthResponse(
    val token: String,
    val userId: Long
)