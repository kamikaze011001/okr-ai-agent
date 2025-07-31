package com.aibles.okragent.modules.chat.infrastructure.persistence.entity

import com.aibles.okragent.modules.chat.domain.model.ChatMessageRole
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "chat_messages")
data class ChatMessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    val id: UUID? = null,

    @Column(name = "chat_session_id", nullable = false)
    val chatSessionId: UUID,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "role", length = 20, nullable = false)
    val role: ChatMessageRole,

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    val content: String,

    @Column(name = "metadata", columnDefinition = "TEXT")
    val metadata: String? = null,

    @Column(name = "response_time_ms")
    val responseTimeMs: Int? = null,

    @Column(name = "confidence_score", precision = 3, scale = 2)
    val confidenceScore: BigDecimal? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)