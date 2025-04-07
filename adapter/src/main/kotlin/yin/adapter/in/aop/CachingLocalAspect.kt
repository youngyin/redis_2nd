package yin.adapter.`in`.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import yin.application.port.out.CacheKeyGeneratorPort
import yin.application.port.out.CaffeineRepositoryPort

@Aspect
@Component
class LocalCachingAspect(
    private val caffeineRepositoryPort: CaffeineRepositoryPort,
    private val cacheKeyGeneratorPort: CacheKeyGeneratorPort
) {
    private val log = LoggerFactory.getLogger(LocalCachingAspect::class.java)

    /**
     * Local Caffeine 캐시 처리
     * - Caffeine → DB 순서로 조회
     * - 캐시가 없으면 DB에서 조회 후 캐시 저장
     */
    @Around("@annotation(localCached)")
    fun cache(joinPoint: ProceedingJoinPoint, localCached: CachedLocal): Any {
        val method = (joinPoint.signature as MethodSignature).method
        val returnType = method.returnType
        val cacheKey = cacheKeyGeneratorPort.generate(joinPoint, localCached.cacheKeyPrefix)

        // Step 1. Caffeine 조회
        caffeineRepositoryPort.getIfPresent(cacheKey, returnType)?.let {
            log.info("✅ [Local Caffeine HIT] key=$cacheKey")
            return it
        }

        // Step 2. DB → 원본 실행
        log.info("📡 [DB QUERY] key=$cacheKey")
        val result = joinPoint.proceed()

        // Step 3. 캐시 저장
        caffeineRepositoryPort.put(cacheKey, result)
        log.info("📥 [Local Caffeine PUT] key=$cacheKey ttl=${localCached.ttlSeconds}s")

        return result
    }
}
