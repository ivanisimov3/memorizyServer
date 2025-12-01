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

// Аналог ViewModel
@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    fun register(request: AuthRequest): AuthResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw RuntimeException("Username is already taken")
        }

        // Создаем пользователя
        val user = User(
            username = request.username,
            passwordHash = passwordEncoder.encode(request.password)
        )

        // Сохраняем в базу
        userRepository.save(user)

        // Сразу выдаем токен, чтобы не заставлять логиниться
        val token = jwtService.generateToken(user.username)
        return AuthResponse(token)
    }

    fun login(request: AuthRequest): AuthResponse {
        // Spring Security сам проверит логин и пароль.
        // Если пароль неверный, он выбросит ошибку (403 Forbidden).
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username,
                request.password
            )
        )

        // Если мы здесь, значит пароль верный. Генерируем токен.
        val user = userRepository.findByUsername(request.username)
            .orElseThrow() // Такого быть не должно после успешной аутентификации

        val token = jwtService.generateToken(user.username)
        return AuthResponse(token)
    }
}