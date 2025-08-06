package com.aibles.okragent.modules.user.infrastructure.config

import com.aibles.okragent.modules.user.infrastructure.persistence.repository.UserRepository
import com.aibles.okragent.modules.user.infrastructure.persistence.repository.NotificationPreferenceRepository
import com.aibles.okragent.modules.user.usecase.GetNotificationPreferenceUseCase
import com.aibles.okragent.modules.user.usecase.InitializeUserUseCase
import com.aibles.okragent.modules.user.usecase.GetUserProfileUseCase
import com.aibles.okragent.modules.user.usecase.UpdateNotificationPreferenceUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserModuleConfig {

    @Bean
    fun initializeUserUseCase(
        userRepository: UserRepository,
        notificationPreferenceRepository: NotificationPreferenceRepository
    ): InitializeUserUseCase {
        return InitializeUserUseCase(userRepository, notificationPreferenceRepository)
    }
    
    @Bean
    fun getUserProfileUseCase(userRepository: UserRepository): GetUserProfileUseCase {
        return GetUserProfileUseCase(userRepository)
    }

    @Bean
    fun getNotificationPreferenceUseCase(
        notificationPreferenceRepository: NotificationPreferenceRepository,
        userRepository: UserRepository
    ): GetNotificationPreferenceUseCase {
        return GetNotificationPreferenceUseCase(
            notificationPreferenceRepository = notificationPreferenceRepository,
            userRepository = userRepository
        )
    }

    @Bean
    fun updateNotificationPreferenceUseCase(
        notificationPreferenceRepository: NotificationPreferenceRepository,
        userRepository: UserRepository
    ): UpdateNotificationPreferenceUseCase {
        return UpdateNotificationPreferenceUseCase(
            notificationPreferenceRepository = notificationPreferenceRepository,
            userRepository = userRepository
        )
    }
}
