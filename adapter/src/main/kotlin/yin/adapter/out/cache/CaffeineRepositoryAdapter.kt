package yin.adapter.out.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Component
import yin.application.port.out.CaffeineRepositoryPort
import java.util.concurrent.TimeUnit

@Component
class CaffeineRepositoryAdapter : CaffeineRepositoryPort {

    // 기본 캐시 설정: 10분 만료
    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build<String, Any>()

    override fun <T> getIfPresent(cacheKey: String, clazz: Class<T>): T? {
        val value = cache.getIfPresent(cacheKey) ?: return null

        // 타입 안전성을 위해 명시적으로 캐스팅
        return try {
            clazz.cast(value)
        } catch (e: ClassCastException) {
            null
        }
    }

    override fun put(cacheKey: String, value: Any) {
        cache.put(cacheKey, value)
    }
}
