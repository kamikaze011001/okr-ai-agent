package com.aibles.okragent.modules.shared.exception

import org.springframework.http.HttpStatus

open class BaseException(
    message: String,
    val errorCode: String,
    val httpStatus: HttpStatus,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class ValidationException(
    message: String,
    errorCode: String = "VAL_001"
) : BaseException(message, errorCode, HttpStatus.BAD_REQUEST)

class UnauthorizedException(
    message: String = "Unauthorized",
    errorCode: String = "AUTH_001"
) : BaseException(message, errorCode, HttpStatus.UNAUTHORIZED)

class ForbiddenException(
    message: String = "Access denied",
    errorCode: String = "AUTH_002"
) : BaseException(message, errorCode, HttpStatus.FORBIDDEN)

class NotFoundException(
    message: String,
    errorCode: String = "NOT_FOUND"
) : BaseException(message, errorCode, HttpStatus.NOT_FOUND)

class InternalServerException(
    message: String,
    errorCode: String = "SYS_001",
    cause: Throwable? = null
) : BaseException(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR, cause)