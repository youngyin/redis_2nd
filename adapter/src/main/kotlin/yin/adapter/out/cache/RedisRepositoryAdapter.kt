package yin.adapter.out.cache

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Component
import yin.application.port.out.RedisRepositoryPort
import java.time.Duration

@Component
class RedisRepositoryAdapter(
    private val redisConnectionFactory: RedisConnectionFactory,
    private val objectMapper: ObjectMapper
) : RedisRepositoryPort {

    private val redisTemplate: RedisTemplate<String, Any> = RedisTemplate<String, Any>().apply {
        setConnectionFactory(redisConnectionFactory)
        keySerializer = StringRedisSerializer()
        valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        afterPropertiesSet()
    }

    /**
     * Redis에서 key에 해당하는 값을 가져온다.
     */
    override fun <T> getIfPresent(key: String, type: Class<T>): T? {
        val rawValue = redisTemplate.opsForValue().get(key) ?: return null
        return objectMapper.convertValue(rawValue, type)
    }

    /**
     * Redis에 key와 value를 저장한다.
     */
    override fun put(key: String, value: Any, ttlSeconds: Long) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds))
    }
}

