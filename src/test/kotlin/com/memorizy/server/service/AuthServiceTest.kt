package com.memorizy.server.service

import com.memorizy.server.dto.AuthRequest
import com.memorizy.server.model.User
import com.memorizy.server.repository.UserRepository
import com.memorizy.server.security.JwtService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

class AuthServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val authenticationManager = mockk<AuthenticationManager>()
    private val jwtService = JwtService(TEST_SECRET, TEST_EXPIRATION)

    private val authService = AuthService(
        userRepository = userRepository,
        passwordEncoder = passwordEncoder,
        jwtService = jwtService,
        authenticationManager = authenticationManager
    )

    @Test
    fun `register creates user and returns token`() {
        val request = AuthRequest(username = "alice", password = "123456")

        every { userRepository.existsByUsername(request.username) } returns false
        every { passwordEncoder.encode(request.password) } returns "hashed-password"
        every { userRepository.save(any()) } answers {
            firstArg<User>().copy(id = 11)  // save обращается к БД и возвращает User с id
        }

        val response = authService.register(request)

        assertEquals(11L, response.userId)
        assertFalse(response.token.isBlank())
        assertEquals(request.username, jwtService.extractUsername(response.token))
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `register with taken username returns 409`() {
        val request = AuthRequest(username = "alice", password = "123456")

        every { userRepository.existsByUsername(request.username) } returns true

        val ex = assertThrows<ResponseStatusException> {
            authService.register(request)
        }

        assertEquals(HttpStatus.CONFLICT, ex.statusCode)
    }

    @Test
    fun `login with valid credentials returns token`() {
        val request = AuthRequest(username = "alice", password = "123456")
        val user = User(id = 7, username = "alice", passwordHash = "hashed")

        every {
            authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>())
        } returns mockk()
        every { userRepository.findByUsername(request.username) } returns Optional.of(user)

        val response = authService.login(request)

        assertEquals(7L, response.userId)
        assertEquals(request.username, jwtService.extractUsername(response.token))
    }

    @Test
    fun `login with invalid credentials returns 401`() {
        val request = AuthRequest(username = "alice", password = "wrong")

        every {
            authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>())
        } throws BadCredentialsException("bad credentials")

        val ex = assertThrows<ResponseStatusException> {
            authService.login(request)
        }

        assertEquals(HttpStatus.UNAUTHORIZED, ex.statusCode)
    }

    private companion object {
        const val TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
        const val TEST_EXPIRATION = 3_600_000L
    }
}