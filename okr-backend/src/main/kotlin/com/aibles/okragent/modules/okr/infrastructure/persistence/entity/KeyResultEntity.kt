package com.aibles.okragent.modules.okr.infrastructure.persistence.entity

import com.aibles.okragent.modules.okr.domain.model.MeasurementType
import com.aibles.okragent.modules.okr.domain.model.KeyResultStatus
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "key_results")
data class KeyResultEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    val id: UUID? = null,

    @Column(name = "objective_id", nullable = false)
    val objectiveId: UUID,

    @Column(name = "title", length = 200, nullable = false)
    val title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "measurement_type", length = 20, nullable = false)
    val measurementType: MeasurementType,

    @Column(name = "current_value", precision = 10, scale = 2)
    val currentValue: BigDecimal = BigDecimal.ZERO,

    @Column(name = "target_value", precision = 10, scale = 2, nullable = false)
    val targetValue: BigDecimal,

    @Column(name = "unit", length = 50)
    val unit: String? = null,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", length = 20, nullable = false)
    val status: KeyResultStatus,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)