package com.aibles.okragent.modules.user.usecase

import com.aibles.okragent.modules.user.infrastructure.persistence.entity.UserEntity
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class GetUserProfileUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var getUserProfileUseCase: GetUserProfileUseCase

    @Test
    fun `should get user profile successfully`() {
        // Given
        val userId = "test-user-id"
        val userEntity = UserEntity(
            id = userId,
            name = "Test User",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(userEntity))
        
        // When
        val result = getUserProfileUseCase.execute(userId)
        
        // Then
        assertEquals(userId, result.id)
        assertEquals("Test User", result.name)
        assertEquals("test@example.com", result.email)
        assertEquals(userEntity.createdAt, result.createdAt)
        assertEquals(userEntity.updatedAt, result.updatedAt)
        
        verify(userRepository).findById(userId)
    }
    
    @Test
    fun `should throw exception when user not found`() {
        // Given
        val userId = "non-existent-user-id"
        `when`(userRepository.findById(userId)).thenReturn(Optional.empty())
        
        // When & Then
        val exception = assertFailsWith<RuntimeException> {
            getUserProfileUseCase.execute(userId)
        }
        
        assertEquals("User not found with id: $userId", exception.message)
        
        verify(userRepository).findById(userId)
    }
}