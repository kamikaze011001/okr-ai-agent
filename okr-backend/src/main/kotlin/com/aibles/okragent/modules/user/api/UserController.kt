package com.aibles.okragent.modules.user.api

import com.aibles.okragent.modules.user.api.dto.UserInitializationResponse
import com.aibles.okragent.modules.user.api.dto.UserResponse
import com.aibles.okragent.modules.user.api.dto.NotificationPreferenceResponse
import com.aibles.okragent.modules.user.api.dto.UpdateNotificationPreferenceRequest
import com.aibles.okragent.modules.user.usecase.InitializeUserUseCase
import com.aibles.okragent.modules.user.usecase.GetUserProfileUseCase
import com.aibles.okragent.modules.user.usecase.GetNotificationPreferenceUseCase
import com.aibles.okragent.modules.user.usecase.UpdateNotificationPreferenceUseCase
import com.aibles.okragent.modules.shared.dto.StandardResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val initializeUserUseCase: InitializeUserUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getNotificationPreferenceUseCase: GetNotificationPreferenceUseCase,
    private val updateNotificationPreferenceUseCase: UpdateNotificationPreferenceUseCase
) {
    
    @PostMapping("/initialize")
    fun initializeUser(
        @AuthenticationPrincipal authentication: Jwt
    ): StandardResponse<UserInitializationResponse> {
        val response = initializeUserUseCase.execute(authentication)
        return StandardResponse(data = response)
    }
    
    @GetMapping("/me")
    fun getMyProfile(
        @AuthenticationPrincipal authentication: Jwt
    ): StandardResponse<UserResponse> {
        val userId = authentication.subject
        val response = getUserProfileUseCase.execute(userId)
        return StandardResponse(data = response)
    }

    @GetMapping("/me/notificationpreferences")
    fun getNotificationPreferences(
        @AuthenticationPrincipal authentication: Jwt
    ): StandardResponse<NotificationPreferenceResponse> {
        val userId = authentication.subject
        val response = getNotificationPreferenceUseCase.execute(userId)
        return StandardResponse(data = response)
    }

    @PutMapping("/me/notificationpreferences")
    fun updateNotificationPreferences(
        @AuthenticationPrincipal authentication: Jwt,
        @Valid @RequestBody request: UpdateNotificationPreferenceRequest
    ): StandardResponse<NotificationPreferenceResponse> {
        val userId = authentication.subject
        val response = updateNotificationPreferenceUseCase.execute(userId, request)
        return StandardResponse(data = response)
    }
}