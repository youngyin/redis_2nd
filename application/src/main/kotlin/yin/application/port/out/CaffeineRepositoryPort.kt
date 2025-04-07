package yin.application.port.out

interface CaffeineRepositoryPort {
    /**
     * Caffeine 캐시에서 조회
     */
    fun <T> getIfPresent(cacheKey: String, clazz: Class<T>): T?

    /**
     * Caffeine 캐시에 저장
     */
    fun put(cacheKey: String, value: Any)
}