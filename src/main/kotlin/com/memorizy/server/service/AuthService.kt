package com.memorizy.server.service

import com.memorizy.server.dto.AuthRequest
import com.memorizy.server.dto.AuthResponse
import com.memorizy.server.model.User
import com.memorizy.server.repository.UserRepository
import com.memorizy.server.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

// A service class that stores business logic

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    // Регистрация пользователя
    fun register(request: AuthRequest): AuthResponse {
        if (userRepository.existsByUsername(request.username)) {    // Проверка на занятость username
            throw RuntimeException("Username is already taken")
        }

        val user = User(
            username = request.username,
            passwordHash = passwordEncoder.encode(request.password)
        )

        userRepository.save(user)

        // Сразу выдаем токен, чтобы не заставлять логиниться
        val token = jwtService.generateToken(user.username)
        return AuthResponse(token, user.id)
    }

    // Авторизация пользователя
    fun login(request: AuthRequest): AuthResponse {

        // Spring Security сам проверит логин и пароль.
        // Если пароль неверный, он выбросит ошибку (403 Forbidden).
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username,
                request.password
            )
        )

        val user = userRepository.findByUsername(request.username)
            .orElseThrow()

        // Выдаем токен
        val token = jwtService.generateToken(user.username)
        return AuthResponse(token, user.id)
    }
}