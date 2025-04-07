package yin.adapter.`in`.aop

import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import yin.application.port.out.RateLimitPort

@Aspect
@Component
class RateLimitAspect(
    private val rateLimitPort: RateLimitPort,
    private val httpServletRequest: HttpServletRequest
) {

    @Around("@annotation(rateLimited)")
    fun checkRateLimit(joinPoint: ProceedingJoinPoint, rateLimited: RateLimited): Any {
        val ip = extractClientIp(httpServletRequest)
        rateLimitPort.checkIpRequestLimit(ip)
        return joinPoint.proceed()
    }

    private fun extractClientIp(request: HttpServletRequest): String {
        return request.getHeader("X-Forwarded-For")?.split(",")?.firstOrNull()
            ?: request.remoteAddr
    }
}