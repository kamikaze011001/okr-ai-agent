package com.aibles.okragent.modules.user.usecase

import com.aibles.okragent.modules.user.api.dto.NotificationPreferenceResponse
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.NotificationPreferenceRepository
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GetNotificationPreferenceUseCase(
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun execute(userId: String): NotificationPreferenceResponse {
        // Ensure user exists using existsById for better performance
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User not found with id: $userId")
        }

        // Get notification preferences
        val preference = notificationPreferenceRepository.findByUserId(userId)
            ?: throw IllegalStateException("Notification preferences not found for user: $userId")

        return NotificationPreferenceResponse(
            id = preference.id!!,
            userId = preference.userId,
            deadlineNotifications = preference.deadlineNotifications,
            progressReminders = preference.progressReminders,
            aiRecommendations = preference.aiRecommendations,
            dailySummary = preference.dailySummary,
            weeklySummary = preference.weeklySummary,
            reminderFrequencyHours = preference.reminderFrequencyHours,
            createdAt = preference.createdAt,
            updatedAt = preference.updatedAt
        )
    }
}