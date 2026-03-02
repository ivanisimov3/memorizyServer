package com.memorizy.server.dto

// Входные данные при авторизации

data class AuthRequest(
    val username: String,
    val password: String
)