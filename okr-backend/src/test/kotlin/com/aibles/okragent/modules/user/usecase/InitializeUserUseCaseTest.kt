package com.aibles.okragent.modules.user.usecase

import com.aibles.okragent.modules.user.infrastructure.persistence.entity.NotificationPreferenceEntity
import com.aibles.okragent.modules.user.infrastructure.persistence.entity.UserEntity
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.NotificationPreferenceRepository
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.oauth2.jwt.Jwt
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class InitializeUserUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var notificationPreferenceRepository: NotificationPreferenceRepository

    @InjectMocks
    private lateinit var initializeUserUseCase: InitializeUserUseCase

    @Test
    fun `should create new user and notification preferences when user does not exist`() {
        // Given
        val keycloakId = "new-user-id"
        val email = "newuser@example.com"
        val name = "New User"
        
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn(keycloakId)
        `when`(jwt.claims).thenReturn(mapOf("email" to email, "name" to name))
        
        val newUser = UserEntity(
            id = keycloakId,
            name = name,
            email = email
        )
        
        `when`(userRepository.existsById(keycloakId)).thenReturn(false)
        `when`(userRepository.save(any(UserEntity::class.java))).thenReturn(newUser)
        
        // When
        val result = initializeUserUseCase.execute(jwt)
        
        // Then
        assertTrue(result.isFirstTime)
        assertEquals(keycloakId, result.userId)
        assertEquals(name, result.name)
        assertEquals(email, result.email)
        
        verify(userRepository).existsById(keycloakId)
        verify(userRepository).save(any(UserEntity::class.java))
        verify(notificationPreferenceRepository).save(any(NotificationPreferenceEntity::class.java))
    }
    
    @Test
    fun `should return existing user when user already exists`() {
        // Given
        val keycloakId = "existing-user-id"
        val email = "existinguser@example.com"
        val name = "Existing User"
        
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn(keycloakId)
        
        val existingUser = UserEntity(
            id = keycloakId,
            name = name,
            email = email
        )
        
        `when`(userRepository.existsById(keycloakId)).thenReturn(true)
        `when`(userRepository.findById(keycloakId)).thenReturn(Optional.of(existingUser))
        
        // When
        val result = initializeUserUseCase.execute(jwt)
        
        // Then
        assertFalse(result.isFirstTime)
        assertEquals(keycloakId, result.userId)
        assertEquals(name, result.name)
        assertEquals(email, result.email)
        
        verify(userRepository).existsById(keycloakId)
        verify(userRepository).findById(keycloakId)
        verify(userRepository, never()).save(any(UserEntity::class.java))
        verify(notificationPreferenceRepository, never()).save(any(NotificationPreferenceEntity::class.java))
    }
}
