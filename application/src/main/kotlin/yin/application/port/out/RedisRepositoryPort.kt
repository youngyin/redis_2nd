package yin.application.port.out

import yin.application.dto.QueryMovieResponse

interface RedisRepositoryPort {
    /**
     * Redis에서 영화 목록을 조회한다.
     *
     * @param cacheKey 캐시 키
     * @return 캐시된 영화 목록
     */
    fun getIfPresent(cacheKey: String): List<QueryMovieResponse>
    /**
     * Redis에 영화 목록을 캐시한다.
     *
     * @param cacheKey 캐시 키
     * @param content 캐시할 영화 목록
     */
    fun put(cacheKey: String, content: List<QueryMovieResponse>)
}