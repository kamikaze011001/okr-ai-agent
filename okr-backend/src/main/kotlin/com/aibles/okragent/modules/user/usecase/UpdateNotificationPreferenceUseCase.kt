package com.aibles.okragent.modules.user.usecase

import com.aibles.okragent.modules.user.api.dto.NotificationPreferenceResponse
import com.aibles.okragent.modules.user.api.dto.UpdateNotificationPreferenceRequest
import com.aibles.okragent.modules.user.infrastructure.persistence.entity.NotificationPreferenceEntity
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.NotificationPreferenceRepository
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class UpdateNotificationPreferenceUseCase(
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun execute(userId: String, request: UpdateNotificationPreferenceRequest): NotificationPreferenceResponse {
        // Ensure user exists using existsById for better performance
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User not found with id: $userId")
        }

        // Get existing notification preferences
        val preference = notificationPreferenceRepository.findByUserId(userId)
            ?: throw IllegalStateException("Notification preferences not found for user: $userId")

        // Update only the fields that are provided in the request (PATCH-like behavior)
        val updatedEntity = NotificationPreferenceEntity(
            id = preference.id,
            userId = preference.userId,
            deadlineNotifications = request.deadlineNotifications ?: preference.deadlineNotifications,
            progressReminders = request.progressReminders ?: preference.progressReminders,
            aiRecommendations = request.aiRecommendations ?: preference.aiRecommendations,
            dailySummary = request.dailySummary ?: preference.dailySummary,
            weeklySummary = request.weeklySummary ?: preference.weeklySummary,
            reminderFrequencyHours = request.reminderFrequencyHours ?: preference.reminderFrequencyHours,
            createdAt = preference.createdAt,
            updatedAt = LocalDateTime.now()
        )

        // Save the updated entity
        val saved = notificationPreferenceRepository.save(updatedEntity)

        return NotificationPreferenceResponse(
            id = saved.id!!,
            userId = saved.userId,
            deadlineNotifications = saved.deadlineNotifications,
            progressReminders = saved.progressReminders,
            aiRecommendations = saved.aiRecommendations,
            dailySummary = saved.dailySummary,
            weeklySummary = saved.weeklySummary,
            reminderFrequencyHours = saved.reminderFrequencyHours,
            createdAt = saved.createdAt,
            updatedAt = saved.updatedAt
        )
    }
}