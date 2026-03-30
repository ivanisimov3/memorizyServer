package com.memorizy.server.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

// Вспомогательный класс для операций с безопасностью

@Service
class JwtService (
    @Value("\${application.security.jwt.secret}")
    private val secretKey: String,

    @Value("\${application.security.jwt.expiration}")
    private val jwtExpiration: Long
){

    // Создание токена (имя пользователя, дата выдачи, дата окончания, подпись секретным ключем)
    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    // Закодирование секретного ключа
    private fun getSigningKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    // Извлечение и возвращение имени из токена
    fun extractUsername(token: String): String {
        return extractAllClaims(token).subject
    }

    // Возвращение Null в случае некорректного токена
    fun extractUsernameOrNull(token: String): String? {
        return runCatching { extractUsername(token) }.getOrNull()
    }

    // Попытка извлечения имени из токена
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .body
    }
}