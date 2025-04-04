package yin.adapter.out

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import yin.application.port.out.CacheHitCounterPort

@Service
class RedisCacheHitCounterAdapter(
    private val redisTemplate: StringRedisTemplate
) : CacheHitCounterPort {

    override fun increment(cacheKey: String) {
        redisTemplate.opsForValue().increment("hit:$cacheKey", 1)
    }

    override fun getHitCount(cacheKey: String): Long {
        return redisTemplate.opsForValue().get("hit:$cacheKey")?.toLongOrNull() ?: 0L
    }
}
