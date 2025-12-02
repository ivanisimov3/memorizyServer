package com.memorizy.server.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

// The filter class that each request to the server passes through

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService  // Core interface which loads user-specific data.
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization") // Проверка на заголовок Authorization в запросе

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {  // Если не авторизация
            filterChain.doFilter(request, response) // Сразу на фильтрацию
            return
        }

        val jwt = authHeader.substring(7)   // Токен без ключевого слова Bearer
        val username = jwtService.extractUsername(jwt)  // Извлекаем имя из токена

        // Если имя существует (зарагестрирован), но не авторизован
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)   // Полные данные пользователя

            // Создаем токен авторизации (полноценный пропуск)
            val authToken = UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.authorities
            )
            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

            // Кладем токен авторизации на сервер
            SecurityContextHolder.getContext().authentication = authToken
        }

        // На фильтрацию
        filterChain.doFilter(request, response)
    }
}