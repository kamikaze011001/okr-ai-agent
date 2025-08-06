package com.aibles.okragent.modules.user.usecase

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
class GetNotificationPreferenceUseCaseTest {

    @Mock
    private lateinit var notificationPreferenceRepository: NotificationPreferenceRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var getNotificationPreferenceUseCase: GetNotificationPreferenceUseCase

    @Test
    fun `should get notification preferences successfully`() {
        // Given
        val userId = "test-user-id"
        val preferenceEntity = NotificationPreferenceEntity(
            id = UUID.randomUUID(),
            userId = userId,
            deadlineNotifications = true,
            progressReminders = false,
            aiRecommendations = true,
            dailySummary = false,
            weeklySummary = true,
            reminderFrequencyHours = 24,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(notificationPreferenceRepository.findByUserId(userId)).thenReturn(preferenceEntity)

        // When
        val result = getNotificationPreferenceUseCase.execute(userId)

        // Then
        assertEquals(preferenceEntity.id, result.id)
        assertEquals(userId, result.userId)
        assertEquals(preferenceEntity.deadlineNotifications, result.deadlineNotifications)
        assertEquals(preferenceEntity.progressReminders, result.progressReminders)
        assertEquals(preferenceEntity.aiRecommendations, result.aiRecommendations)
        assertEquals(preferenceEntity.dailySummary, result.dailySummary)
        assertEquals(preferenceEntity.weeklySummary, result.weeklySummary)
        assertEquals(preferenceEntity.reminderFrequencyHours, result.reminderFrequencyHours)
        assertEquals(preferenceEntity.createdAt, result.createdAt)
        assertEquals(preferenceEntity.updatedAt, result.updatedAt)

        verify(userRepository).existsById(userId)
        verify(notificationPreferenceRepository).findByUserId(userId)
    }

    @Test
    fun `should throw exception when user not found`() {
        // Given
        val userId = "non-existent-user-id"
        `when`(userRepository.existsById(userId)).thenReturn(false)

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            getNotificationPreferenceUseCase.execute(userId)
        }

        assertEquals("User not found with id: $userId", exception.message)

        verify(userRepository).existsById(userId)
        verify(notificationPreferenceRepository, never()).findByUserId(anyString())
    }

    @Test
    fun `should throw exception when notification preferences not found`() {
        // Given
        val userId = "test-user-id"
        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(notificationPreferenceRepository.findByUserId(userId)).thenReturn(null)

        // When & Then
        val exception = assertFailsWith<IllegalStateException> {
            getNotificationPreferenceUseCase.execute(userId)
        }

        assertEquals("Notification preferences not found for user: $userId", exception.message)

        verify(userRepository).existsById(userId)
        verify(notificationPreferenceRepository).findByUserId(userId)
    }
}