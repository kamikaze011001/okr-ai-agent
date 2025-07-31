package com.aibles.okragent.modules.calendar.infrastructure.persistence.entity

import com.aibles.okragent.modules.calendar.domain.model.TimeBlockCategory
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "time_blocks")
data class TimeBlockEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    val id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "title", length = 200, nullable = false)
    val title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "category", length = 20, nullable = false)
    val category: TimeBlockCategory,

    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,

    @Column(name = "end_time", nullable = false)
    val endTime: LocalDateTime,

    @Column(name = "is_recurring", nullable = false)
    val isRecurring: Boolean = false,

    @Column(name = "recurrence_pattern", length = 100)
    val recurrencePattern: String? = null,

    @Column(name = "color_code", length = 7)
    val colorCode: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)