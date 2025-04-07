package yin.infrastructure.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
class RateLimitException(
    override val message: String = "요청 제한을 초과했습니다."
) : RuntimeException(message)