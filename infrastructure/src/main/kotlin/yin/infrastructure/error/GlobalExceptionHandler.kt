package yin.infrastructure.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * 요청 제한 초과 예외 처리
     */
    @ExceptionHandler(RateLimitException::class)
    fun handleRateLimitException(ex: RateLimitException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = 429,
            code = "RATE_LIMIT_EXCEEDED",
            message = ex.message ?: "Too Many Requests",
            data = null
        )
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response)
    }
}