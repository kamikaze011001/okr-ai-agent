package com.aibles.okragent.modules.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StandardResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: String = "Success",
    val timestamp: LocalDateTime = LocalDateTime.now()
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val success: Boolean = false,
    val data: Any? = null,
    val message: String = "Error occurred",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val error: ErrorDetails? = null
)

data class ErrorDetails(
    val code: String? = null,
    val details: List<ValidationError>? = null
)

data class ValidationError(
    val field: String? = null,
    val message: String? = null
)

data class PaginationInfo(
    val page: Int,
    val size: Int,
    val totalItems: Long,
    val totalPages: Int
)