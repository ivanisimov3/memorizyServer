package com.memorizy.server.service

import com.memorizy.server.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

// A service class that stores business logic

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found") }

        // Превращаем обычного User в UserDetails для Spring Security
        return User.builder()
            .username(user.username)
            .password(user.passwordHash)
            .roles("USER")
            .build()
    }
}