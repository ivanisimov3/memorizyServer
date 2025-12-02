package com.memorizy.server.controller

import com.memorizy.server.dto.AuthRequest
import com.memorizy.server.dto.AuthResponse
import com.memorizy.server.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// Controller, the task is to accept the request, send it to the correct service, receive a response and send it back.

@RestController
@RequestMapping("/api/auth") // Все запросы начинаются с этого адреса
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.register(request))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.login(request))
    }
}