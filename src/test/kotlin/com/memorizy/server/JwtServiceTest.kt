package com.memorizy.server

import com.memorizy.server.security.JwtService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JwtServiceTest {

    private val testKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
    private val testExpiration = 1000L * 60 * 60 // 1 час

    // Создаем сервис вручную (без Spring), так как это Unit-тест
    private val jwtService = JwtService(testKey, testExpiration)

    @Test
    fun `generateToken creates valid token able to extract username`() {
        // 1. Придумываем имя
        val username = "testUser"

        // 2. Генерируем токен
        val token = jwtService.generateToken(username)

        // Проверяем, что токен не пустой
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
        println("Generated Token: $token") // Выведет токен в консоль

        // 3. Пытаемся извлечь имя обратно
        val extractedUsername = jwtService.extractUsername(token)

        // 4. Проверяем, что достали то же самое, что положили
        assertEquals(username, extractedUsername)
    }
}