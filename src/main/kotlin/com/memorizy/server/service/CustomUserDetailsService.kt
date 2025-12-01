package com.memorizy.server.service

import com.memorizy.server.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found") }

        // Мы берем НАШЕГО user и превращаем в SPRING user
        return User.builder()
            .username(user.username)
            .password(user.passwordHash) // Spring сам сверит пароль с хэшем
            .roles("USER") // Пока у всех роль USER
            .build()
    }
}