package yin.infrastructure.error

/**
 * ErrorResponse is a data class that represents the structure of an error response.
 */
data class ErrorResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: Any? = null
)