package yin.application.port.out

import org.aspectj.lang.ProceedingJoinPoint

interface CacheKeyGeneratorPort {
    /**
     * prefix와 메서드 파라미터들을 이용하여 캐시 키 생성
     */
    fun generate(joinPoint: ProceedingJoinPoint, prefix: String): String
}