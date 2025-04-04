package yin.adapter.out.caffine

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Component
import yin.application.dto.QueryMovieResponse
import yin.application.port.out.CaffeineRepositoryPort
import java.util.concurrent.TimeUnit

@Component
class CaffeineRepositoryAdapter : CaffeineRepositoryPort {

    // 싱글톤 캐시 인스턴스를 직접 생성
    private val movieCache: Cache<String, List<QueryMovieResponse>> =
        Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build()

    override fun getIfPresent(key: String): List<QueryMovieResponse> {
        val value = movieCache.getIfPresent(key)
        if (value != null) {
            //println("✅ [Caffeine HIT] key = $key")
        } else {
            //println("⚠️ [Caffeine MISS] key = $key")
        }
        return value ?: emptyList()
    }

    override fun put(key: String, value: List<QueryMovieResponse>) {
        //println("📦 [Caffeine PUT] key = $key, size = ${value.size}")
        movieCache.put(key, value)
    }
}
