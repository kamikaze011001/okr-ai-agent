package com.aibles.okragent.exception

import com.aibles.okragent.modules.shared.dto.ErrorDetails
import com.aibles.okragent.modules.shared.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.jwt.JwtValidationException
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        ex: AuthenticationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            success = false,
            data = null,
            message = "Unauthorized: ${ex.message}",
            timestamp = LocalDateTime.now(),
            error = ErrorDetails(
                code = "AUTH_001",
                details = null
            )
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(JwtValidationException::class)
    fun handleJwtValidationException(
        ex: JwtValidationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            success = false,
            data = null,
            message = "Invalid token: ${ex.message}",
            timestamp = LocalDateTime.now(),
            error = ErrorDetails(
                code = "AUTH_001",
                details = null
            )
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(
        ex: JwtException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            success = false,
            data = null,
            message = "Invalid token: ${ex.message}",
            timestamp = LocalDateTime.now(),
            error = ErrorDetails(
                code = "AUTH_001",
                details = null
            )
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(OAuth2AuthenticationException::class)
    fun handleOAuth2AuthenticationException(
        ex: OAuth2AuthenticationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            success = false,
            data = null,
            message = "Authentication failed: ${ex.message}",
            timestamp = LocalDateTime.now(),
            error = ErrorDetails(
                code = "AUTH_001",
                details = null
            )
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            success = false,
            data = null,
            message = ex.message ?: "An unexpected error occurred",
            timestamp = LocalDateTime.now(),
            error = ErrorDetails(
                code = "SYS_001",
                details = null
            )
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
