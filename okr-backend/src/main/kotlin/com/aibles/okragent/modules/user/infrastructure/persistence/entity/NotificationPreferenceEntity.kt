package com.aibles.okragent.modules.user.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "notification_preferences")
data class NotificationPreferenceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    val id: UUID? = null,

    @Column(name = "user_id", nullable = false, unique = true)
    val userId: String,

    @Column(name = "deadline_notifications", nullable = false)
    val deadlineNotifications: Boolean = true,

    @Column(name = "progress_reminders", nullable = false)
    val progressReminders: Boolean = true,

    @Column(name = "ai_recommendations", nullable = false)
    val aiRecommendations: Boolean = true,

    @Column(name = "daily_summary", nullable = false)
    val dailySummary: Boolean = false,

    @Column(name = "weekly_summary", nullable = false)
    val weeklySummary: Boolean = true,

    @Column(name = "reminder_frequency_hours", nullable = false)
    val reminderFrequencyHours: Int = 24,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)