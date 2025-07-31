package com.aibles.okragent.modules.okr.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "progress_history")
data class ProgressHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    val id: UUID? = null,

    @Column(name = "key_result_id", nullable = false)
    val keyResultId: UUID,

    @Column(name = "previous_value", precision = 10, scale = 2)
    val previousValue: BigDecimal? = null,

    @Column(name = "new_value", precision = 10, scale = 2)
    val newValue: BigDecimal? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(name = "recorded_at", nullable = false)
    val recordedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_by", nullable = false)
    val updatedBy: String
)