package com.memorizy.server.service

import com.memorizy.server.dto.StudySetDto
import com.memorizy.server.model.StudySet
import com.memorizy.server.model.User
import com.memorizy.server.repository.StudySetRepository
import com.memorizy.server.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

// A service class that stores business logic

@Service
class StudySetService(
    private val studySetRepository: StudySetRepository,
    private val userRepository: UserRepository
) {

    // Найти текущего User в базе
    private fun getCurrentUser(): User {
        val username = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found") }
    }

    // Создать набор у текущего пользователя
    fun createStudySet(dto: StudySetDto): StudySetDto {
        val currentUser = getCurrentUser()

        val studySet = StudySet(
            name = dto.name,
            description = dto.description,
            iconId = dto.iconId,
            user = currentUser
        )

        val savedSet = studySetRepository.save(studySet)

        return dto.copy(id = savedSet.id)   // Возвращаем DTO с новым ID
    }

    // Получить все наборы текущего пользователя
    fun getAllStudySets(): List<StudySetDto> {
        val currentUser = getCurrentUser()

        return studySetRepository.findAllByUserUsername(currentUser.username).map { set ->
            StudySetDto(
                id = set.id,
                name = set.name,
                description = set.description,
                iconId = set.iconId,
                createdAt = set.createdAt
            )
        }
    }

    // Обновить информацию набора текущего пользователя
    fun updateStudySet(id: Long, dto: StudySetDto): StudySetDto {
        val existingSet = studySetRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found") }

        val currentUser = getCurrentUser()

        if (existingSet.user.username != currentUser.username) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this set")
        }

        val updatedSet = existingSet.copy(
            name = dto.name,
            description = dto.description,
            iconId = dto.iconId
        )

        studySetRepository.save(updatedSet)

        return dto.copy(id = id, createdAt = existingSet.createdAt)
    }

    // Удалить набор у текущего пользователя
    fun deleteStudySet(setId: Long) {
        val studySet = studySetRepository.findById(setId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found") }

        val currentUser = getCurrentUser()

        if (studySet.user.username != currentUser.username) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this set")
        }

        studySetRepository.delete(studySet)
    }
}