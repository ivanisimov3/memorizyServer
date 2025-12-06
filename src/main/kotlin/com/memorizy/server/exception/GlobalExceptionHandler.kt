package com.memorizy.server.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

// Вспомогательный класс, чтобы отправлять на клиент корректные коды ошибок (Spring Security менял все на 403)
@RestControllerAdvice
class GlobalExceptionHandler {

    // Ловим ResponseStatusException
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: ResponseStatusException): ResponseEntity<String> {

        return ResponseEntity
            .status(e.statusCode)
            .body(e.reason ?: e.message)
    }
}