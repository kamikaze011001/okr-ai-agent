package com.aibles.okragent.modules.user.api.dto

import java.time.LocalDateTime

data class UserResponse(
    val id: String,
    val name: String?,
    val email: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)