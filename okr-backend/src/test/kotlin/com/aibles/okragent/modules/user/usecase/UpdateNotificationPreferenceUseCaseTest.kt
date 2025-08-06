package com.aibles.okragent.modules.user.usecase

import com.aibles.okragent.modules.user.api.dto.UpdateNotificationPreferenceRequest
import com.aibles.okragent.modules.user.infrastructure.persistence.entity.NotificationPreferenceEntity
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.NotificationPreferenceRepository
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class UpdateNotificationPreferenceUseCaseTest {

    @Mock
    private lateinit var notificationPreferenceRepository: NotificationPreferenceRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var updateNotificationPreferenceUseCase: UpdateNotificationPreferenceUseCase

    @Test
    fun `should update notification preferences successfully`() {
        // Given
        val userId = "test-user-id"
        val request = UpdateNotificationPreferenceRequest(
            deadlineNotifications = false,
            progressReminders = true,
            aiRecommendations = false,
            dailySummary = true,
            weeklySummary = false,
            reminderFrequencyHours = 12
        )

        val existingEntity = NotificationPreferenceEntity(
            id = UUID.randomUUID(),
            userId = userId,
            deadlineNotifications = true,
            progressReminders = false,
            aiRecommendations = true,
            dailySummary = false,
            weeklySummary = true,
            reminderFrequencyHours = 24,
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now().minusDays(1)
        )

        val updatedEntity = NotificationPreferenceEntity(
            id = existingEntity.id,
            userId = userId,
            deadlineNotifications = false,
            progressReminders = true,
            aiRecommendations = false,
            dailySummary = true,
            weeklySummary = false,
            reminderFrequencyHours = 12,
            createdAt = existingEntity.createdAt,
            updatedAt = LocalDateTime.now()
        )

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(notificationPreferenceRepository.findByUserId(userId)).thenReturn(existingEntity)
        `when`(notificationPreferenceRepository.save(any(NotificationPreferenceEntity::class.java))).thenReturn(updatedEntity)

        // When
        val result = updateNotificationPreferenceUseCase.execute(userId, request)

        // Then
        assertEquals(updatedEntity.id, result.id)
        assertEquals(userId, result.userId)
        assertEquals(false, result.deadlineNotifications)
        assertEquals(true, result.progressReminders)
        assertEquals(false, result.aiRecommendations)
        assertEquals(true, result.dailySummary)
        assertEquals(false, result.weeklySummary)
        assertEquals(12, result.reminderFrequencyHours)

        verify(userRepository).existsById(userId)
        verify(notificationPreferenceRepository).findByUserId(userId)
        verify(notificationPreferenceRepository).save(any(NotificationPreferenceEntity::class.java))
    }

    @Test
    fun `should perform partial update when only some fields are provided`() {
        // Given
        val userId = "test-user-id"
        val request = UpdateNotificationPreferenceRequest(
            deadlineNotifications = false,
            aiRecommendations = false
        )

        val existingEntity = NotificationPreferenceEntity(
            id = UUID.randomUUID(),
            userId = userId,
            deadlineNotifications = true,
            progressReminders = true,
            aiRecommendations = true,
            dailySummary = false,
            weeklySummary = true,
            reminderFrequencyHours = 24,
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now().minusDays(1)
        )

        val updatedEntity = NotificationPreferenceEntity(
            id = existingEntity.id,
            userId = userId,
            deadlineNotifications = false, // Updated
            progressReminders = true, // Unchanged
            aiRecommendations = false, // Updated
            dailySummary = false, // Unchanged
            weeklySummary = true, // Unchanged
            reminderFrequencyHours = 24, // Unchanged
            createdAt = existingEntity.createdAt,
            updatedAt = LocalDateTime.now()
        )

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(notificationPreferenceRepository.findByUserId(userId)).thenReturn(existingEntity)
        `when`(notificationPreferenceRepository.save(any(NotificationPreferenceEntity::class.java))).thenReturn(updatedEntity)

        // When
        val result = updateNotificationPreferenceUseCase.execute(userId, request)

        // Then
        assertEquals(updatedEntity.id, result.id)
        assertEquals(userId, result.userId)
        assertEquals(false, result.deadlineNotifications) // Updated
        assertEquals(true, result.progressReminders) // Unchanged
        assertEquals(false, result.aiRecommendations) // Updated
        assertEquals(false, result.dailySummary) // Unchanged
        assertEquals(true, result.weeklySummary) // Unchanged
        assertEquals(24, result.reminderFrequencyHours) // Unchanged

        verify(userRepository).existsById(userId)
        verify(notificationPreferenceRepository).findByUserId(userId)
        verify(notificationPreferenceRepository).save(any(NotificationPreferenceEntity::class.java))
    }

    @Test
    fun `should throw exception when user not found`() {
        // Given
        val userId = "non-existent-user-id"
        val request = UpdateNotificationPreferenceRequest()

        `when`(userRepository.existsById(userId)).thenReturn(false)

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            updateNotificationPreferenceUseCase.execute(userId, request)
        }

        assertEquals("User not found with id: $userId", exception.message)

        verify(userRepository).existsById(userId)
        verify(notificationPreferenceRepository, never()).findByUserId(anyString())
        verify(notificationPreferenceRepository, never()).save(any(NotificationPreferenceEntity::class.java))
    }

    @Test
    fun `should throw exception when notification preferences not found`() {
        // Given
        val userId = "test-user-id"
        val request = UpdateNotificationPreferenceRequest()

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(notificationPreferenceRepository.findByUserId(userId)).thenReturn(null)

        // When & Then
        val exception = assertFailsWith<IllegalStateException> {
            updateNotificationPreferenceUseCase.execute(userId, request)
        }

        assertEquals("Notification preferences not found for user: $userId", exception.message)

        verify(userRepository).existsById(userId)
        verify(notificationPreferenceRepository).findByUserId(userId)
        verify(notificationPreferenceRepository, never()).save(any(NotificationPreferenceEntity::class.java))
    }
}