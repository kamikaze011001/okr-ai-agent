package com.aibles.okragent.modules.user.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

data class NotificationPreferenceResponse(
    val id: UUID,
    @JsonProperty("userId")
    val userId: String,
    @JsonProperty("deadlineNotifications")
    val deadlineNotifications: Boolean,
    @JsonProperty("progressReminders")
    val progressReminders: Boolean,
    @JsonProperty("aiRecommendations")
    val aiRecommendations: Boolean,
    @JsonProperty("dailySummary")
    val dailySummary: Boolean,
    @JsonProperty("weeklySummary")
    val weeklySummary: Boolean,
    @JsonProperty("reminderFrequencyHours")
    val reminderFrequencyHours: Int,
    @JsonProperty("createdAt")
    val createdAt: LocalDateTime,
    @JsonProperty("updatedAt")
    val updatedAt: LocalDateTime
)