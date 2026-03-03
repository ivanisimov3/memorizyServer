package com.memorizy.server.dto

// Выходные данные из сервера при авторизации

data class AuthResponse(
    val token: String,
    val userId: Long
)