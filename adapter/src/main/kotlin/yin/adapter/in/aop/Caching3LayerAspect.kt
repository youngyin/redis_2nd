package yin.adapter.`in`.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import yin.application.port.out.*

@Aspect
@Component
class Caching3LayerAspect(
    private val caffeineRepositoryPort: CaffeineRepositoryPort,
    private val redisRepositoryPort: RedisRepositoryPort,
    private val cacheKeyGeneratorPort: CacheKeyGeneratorPort,
    private val cacheHitCounterPort: CacheHitCounterPort
) {

    private val log = LoggerFactory.getLogger(Caching3LayerAspect::class.java)

    /**
     * 3계층 캐싱을 위한 Aspect
     * Caffeine → Redis → DB 순으로 조회
     * @param joinPoint ProceedingJoinPoint
     * @param cached3Layer Cached3Layer
     * @return Any
     */
    @Around("@annotation(cached3Layer)")
    fun cache(joinPoint: ProceedingJoinPoint, cached3Layer: Cached3Layer): Any {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val returnType = method.returnType
        val cacheKey = cacheKeyGeneratorPort.generate(joinPoint, cached3Layer.cacheKeyPrefix)

        // Step 1. Caffeine 조회
        val caffeineHit = caffeineRepositoryPort.getIfPresent(cacheKey, returnType)
        if (caffeineHit != null) {
            log.info("✅ Caffeine Cache HIT [key=$cacheKey]")
            cacheHitCounterPort.increment(cacheKey)
            return caffeineHit
        }

        // Step 2. Redis 조회
        val redisHit = redisRepositoryPort.getIfPresent(cacheKey, returnType)
        if (redisHit != null) {
            log.info("✅ Redis Cache HIT [key=$cacheKey]")
            caffeineRepositoryPort.put(cacheKey, redisHit)
            cacheHitCounterPort.increment(cacheKey)
            return redisHit
        }

        // Step 3. DB (실제 메서드 실행)
        log.info("📡 DB QUERY [key=$cacheKey]")
        val result = joinPoint.proceed()

        // Step 4. Caffeine 저장
        caffeineRepositoryPort.put(cacheKey, result)

        // Step 5. Redis 저장 조건 체크
        val hitCount = cacheHitCounterPort.getHitCount(cacheKey)
        if (hitCount >= cached3Layer.syncThreshold) {
            redisRepositoryPort.put(cacheKey, result, cached3Layer.ttlSeconds)
            log.info("📦 Redis PUT [key=$cacheKey] (hitCount=$hitCount)")
        } else {
            log.info("🚫 Redis SKIP [key=$cacheKey] (hitCount=$hitCount)")
        }

        return result
    }

}