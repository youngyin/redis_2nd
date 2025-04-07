package yin.application.service.cache

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse
import yin.application.port.`in`.cache.LocalCacheMovieUseCase
import yin.application.port.out.*
import yin.infrastructure.util.PageUtils.toPage

@Transactional(readOnly = true)
@Service
class LocalCacheMovieService (
    private val movieRepositoryPort: MovieRepositoryPort,
    private val caffeineRepositoryPort: CaffeineRepositoryPort,
    private val cacheKeyGeneratorPort: CacheKeyGeneratorPort,
    private val cacheHitCounterPort: CacheHitCounterPort
) : LocalCacheMovieUseCase {
    private val log = org.slf4j.LoggerFactory.getLogger(LocalCacheMovieService::class.java)

    /**
     * 영화 목록 조회 (캐시 O)
     * - Caffeine → DB 순서로 조회
     */
    override fun findAllMovies(
        command: QueryMovieCommand,
        pageable: Pageable
    ): Page<QueryMovieResponse> {
        val cacheKey = cacheKeyGeneratorPort.generateMovieKey(command)

        val cached = findFromCache(cacheKey)
        if (cached != null) return cached.toPage(pageable)

        val loaded = loadFromDatabase(command, pageable)
        saveToCache(cacheKey, loaded)

        return loaded.toPage(pageable)
    }

    /**
     * 캐시 조회
     */
    private fun findFromCache(cacheKey: String): List<QueryMovieResponse>? {
        caffeineRepositoryPort.getIfPresent(cacheKey).takeIf { it.isNotEmpty() }?.let {
            log.info("✅ [Caffeine HIT] key = $cacheKey")
            cacheHitCounterPort.increment(cacheKey)
            return it
        }

        log.info("⚠️ [Caffeine MISS] key = $cacheKey")
        return null
    }

    /**
     * DB 조회
     */
    private fun loadFromDatabase(
        command: QueryMovieCommand,
        pageable: Pageable
    ): List<QueryMovieResponse> {
        log.info("📡 [DB QUERY] command = $command")
        val movieList = movieRepositoryPort.findAllMovies(command, pageable)
        val schedules = movieRepositoryPort.findSchedulesByMovieIdIn(movieList.content.map { it.id })
        val scheduleMap = schedules.groupBy { it.movieId }

        return movieList.content.map { movie ->
            val movieSchedules = scheduleMap[movie.id] ?: emptyList()
            movie.copy(schedules = movieSchedules)
        }
    }

    /**
     * 캐시 저장 (Caffeine)
     */
    private fun saveToCache(cacheKey: String, responseList: List<QueryMovieResponse>) {
        caffeineRepositoryPort.put(cacheKey, responseList)
        log.info("📥 [Caffeine PUT] (from DB) key = $cacheKey")
    }
}