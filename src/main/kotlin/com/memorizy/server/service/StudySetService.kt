package com.memorizy.server.service

import com.memorizy.server.dto.StudySetDto
import com.memorizy.server.model.StudySet
import com.memorizy.server.repository.StudySetRepository
import com.memorizy.server.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class StudySetService(
    private val studySetRepository: StudySetRepository,
    private val userRepository: UserRepository
) {

    // Вспомогательная функция: Получить текущего залогиненного юзера
    private fun getCurrentUser(): com.memorizy.server.model.User {
        val username = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found") }
    }

    // Создать набор
    fun createStudySet(dto: StudySetDto): StudySetDto {
        val currentUser = getCurrentUser()

        val studySet = StudySet(
            name = dto.name,
            description = dto.description,
            iconId = dto.iconId,
            user = currentUser // Привязываем к текущему пользователю
        )

        val savedSet = studySetRepository.save(studySet)

        return dto.copy(id = savedSet.id) // Возвращаем DTO с новым ID
    }

    // Получить ВСЕ наборы текущего пользователя
    fun getAllStudySets(): List<StudySetDto> {
        val currentUser = getCurrentUser()

        return studySetRepository.findAllByUserUsername(currentUser.username)
            .map { set ->
                StudySetDto(
                    id = set.id,
                    name = set.name,
                    description = set.description,
                    iconId = set.iconId
                )
            }
    }
}