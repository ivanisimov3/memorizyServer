package com.memorizy.server.dto

// DTO (Data Transfer Object) - objects between client and server

data class AuthRequest(
    val username: String,
    val password: String
)