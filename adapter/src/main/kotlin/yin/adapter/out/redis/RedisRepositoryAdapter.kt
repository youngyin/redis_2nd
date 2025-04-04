package yin.adapter.out.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Component
import yin.application.dto.QueryMovieResponse
import yin.application.port.out.RedisRepositoryPort
import java.time.Duration

@Component
class RedisRepositoryAdapter(
    redisConnectionFactory: RedisConnectionFactory,
    objectMapper: ObjectMapper // ← 등록된 커스텀 mapper 주입받기
) : RedisRepositoryPort {

    private val redisTemplate: RedisTemplate<String, List<QueryMovieResponse>> = RedisTemplate<String, List<QueryMovieResponse>>().apply {
        setConnectionFactory(redisConnectionFactory)
        keySerializer = StringRedisSerializer()
        valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        afterPropertiesSet() // 꼭 호출해줘야 함
    }

    override fun getIfPresent(key: String): List<QueryMovieResponse> {
        return redisTemplate.opsForValue().get(key) ?: emptyList()
    }

    override fun put(key: String, value: List<QueryMovieResponse>) {
        //println("📦 [Redis PUT] key = $key, size = ${value.size}")
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10))
    }
}
