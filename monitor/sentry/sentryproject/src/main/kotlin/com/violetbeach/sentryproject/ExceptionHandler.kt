package com.violetbeach.sentryproject

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(value = [RuntimeException::class])
    fun indexOutOfBoundException(e: RuntimeException): String {
        return "Error 발생"
    }
}
