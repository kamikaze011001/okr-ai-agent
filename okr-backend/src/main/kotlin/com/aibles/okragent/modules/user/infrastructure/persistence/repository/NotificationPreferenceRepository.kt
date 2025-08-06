package com.aibles.okragent.modules.user.infrastructure.persistence.repository

import com.aibles.okragent.modules.user.infrastructure.persistence.entity.NotificationPreferenceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NotificationPreferenceRepository : JpaRepository<NotificationPreferenceEntity, String> {
    // JpaRepository already provides findById, save, etc.
    // Add any custom query methods here if needed
    @Query("SELECT n FROM NotificationPreferenceEntity n WHERE n.userId = ?1")
    fun findByUserId(userId: String): NotificationPreferenceEntity?
}
