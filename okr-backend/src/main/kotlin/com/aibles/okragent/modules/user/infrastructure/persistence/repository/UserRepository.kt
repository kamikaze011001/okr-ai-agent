package com.aibles.okragent.modules.user.infrastructure.persistence.repository

import com.aibles.okragent.modules.user.infrastructure.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, String> {
    // JpaRepository already provides findById, save, etc.
    // Add any custom query methods here if needed
}