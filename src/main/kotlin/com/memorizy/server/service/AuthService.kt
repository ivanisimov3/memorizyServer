package com.memorizy.server.service

import com.memorizy.server.dto.AuthRequest
import com.memorizy.server.dto.AuthResponse
import com.memorizy.server.model.User
import com.memorizy.server.repository.UserRepository
import com.memorizy.server.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

// Слой бизнес логики для авторизации

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
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username '${request.username}' is already taken")
        }

        val user = User(
            username = request.username,
            passwordHash = passwordEncoder.encode(request.password)
        )

        val savedUser = userRepository.save(user)

        // Сразу выдаем токен, чтобы не заставлять логиниться
        val token = jwtService.generateToken(savedUser.username)
        return AuthResponse(token, savedUser.id)
    }

    // Авторизация пользователя
    fun login(request: AuthRequest): AuthResponse {

        try {
            authenticationManager.authenticate( // Проверка на соответствие логина и пароля
                UsernamePasswordAuthenticationToken(
                    request.username,
                    request.password
                )
            )
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password")
        }

        val user = userRepository.findByUsername(request.username)
            .orElseThrow()

        // Выдаем токен
        val token = jwtService.generateToken(user.username)
        return AuthResponse(token, user.id)
    }
}