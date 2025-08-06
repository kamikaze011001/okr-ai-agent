package com.aibles.okragent.modules.user.usecase

import com.aibles.okragent.modules.user.infrastructure.persistence.repository.UserRepository
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.NotificationPreferenceRepository
import com.aibles.okragent.modules.user.infrastructure.persistence.entity.UserEntity
import com.aibles.okragent.modules.user.infrastructure.persistence.entity.NotificationPreferenceEntity
import com.aibles.okragent.modules.user.api.dto.UserInitializationResponse
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
class InitializeUserUseCase(
    private val userRepository: UserRepository,
    private val notificationPreferenceRepository: NotificationPreferenceRepository
) {
    
    fun execute(authentication: Jwt): UserInitializationResponse {
        val keycloakId = authentication.subject
        
        // Check if user exists
        val isFirstTime = !userRepository.existsById(keycloakId)
        
        // Get or create user
        val user = if (isFirstTime) {
            val email = authentication.claims["email"] as String?
            val name = authentication.claims["name"] as String?
            
            val newUser = UserEntity(
                id = keycloakId,
                email = email,
                name = name
            )
            val savedUser = userRepository.save(newUser)
            
            // Create notification preferences for new user
            val notificationPreferences = NotificationPreferenceEntity(
                userId = keycloakId
            )
            notificationPreferenceRepository.save(notificationPreferences)
            
            savedUser
        } else {
            userRepository.findById(keycloakId).get()
        }
        
        return UserInitializationResponse(
            userId = user.id,
            name = user.name,
            email = user.email,
            isFirstTime = isFirstTime
        )
    }
}
