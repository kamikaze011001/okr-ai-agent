package com.aibles.okragent.modules.user.usecase

import com.aibles.okragent.modules.user.infrastructure.persistence.repository.UserRepository
import com.aibles.okragent.modules.user.api.dto.UserResponse
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    
    fun execute(userId: String): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { throw RuntimeException("User not found with id: $userId") }
            
        return UserResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}