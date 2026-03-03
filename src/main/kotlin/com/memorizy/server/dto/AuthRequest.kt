package com.memorizy.server.dto

// Входные данные на сервер при авторизации

data class AuthRequest(
    val username: String,
    val password: String
)