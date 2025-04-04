package yin.application.port.out

interface CacheHitCounterPort {
    /**
     * 캐시 적중 카운트를 증가시킨다.
     *
     * @param cacheKey 캐시 키
     */
    fun increment(cacheKey: String)

    /**
     * 캐시 적중 카운트를 조회한다.
     *
     * @param cacheKey 캐시 키
     * @return 캐시 적중 카운트
     */
    fun getHitCount(cacheKey: String): Long
}