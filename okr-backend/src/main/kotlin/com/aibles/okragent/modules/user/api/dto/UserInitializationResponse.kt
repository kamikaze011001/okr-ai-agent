package com.aibles.okragent.modules.user.api.dto

data class UserInitializationResponse(
    val userId: String,
    val name: String?,
    val email: String?,
    val isFirstTime: Boolean
)