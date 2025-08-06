package com.aibles.okragent.modules.user.api.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class UpdateNotificationPreferenceRequest(
    val deadlineNotifications: Boolean? = null,
    val progressReminders: Boolean? = null,
    val aiRecommendations: Boolean? = null,
    val dailySummary: Boolean? = null,
    val weeklySummary: Boolean? = null,
    
    @field:Min(1, message = "Reminder frequency must be at least 1 hour")
    @field:Max(168, message = "Reminder frequency must be at most 168 hours (1 week)")
    val reminderFrequencyHours: Int? = null
)