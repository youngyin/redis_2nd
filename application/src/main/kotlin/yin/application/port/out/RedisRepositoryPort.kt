package yin.application.port.out

interface RedisRepositoryPort {
    /**
     * Redis에서 데이터를 조회한다.
     */
    fun <T> getIfPresent(key: String, type: Class<T>): T?

    /**
     * Redis에 데이터를 저장한다.
     */
    fun put(key: String, value: Any, ttlSeconds: Long = 600)
}
