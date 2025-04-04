package yin.application.port.out

import yin.application.dto.QueryMovieResponse

interface CaffeineRepositoryPort {
    /**
     * Caffeine 캐시에서 영화 목록을 조회
     *
     * @param cacheKey 캐시 키
     * @return 영화 목록
     */
    fun getIfPresent(cacheKey: String): List<QueryMovieResponse>
    /**
     * Caffeine 캐시에 영화 목록을 저장
     *
     * @param cacheKey 캐시 키
     * @param content 영화 목록
     */
    fun put(cacheKey: String, content: List<QueryMovieResponse>)
}