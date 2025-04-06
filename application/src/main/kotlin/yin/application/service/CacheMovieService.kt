package yin.application.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse
import yin.infrastructure.util.PageUtils.toPage
import yin.application.port.`in`.CacheMovieUseCase
import yin.application.port.out.*

@Service
class CacheMovieService(
    private val movieRepositoryPort: MovieRepositoryPort,
    private val caffeineRepositoryPort: CaffeineRepositoryPort,
    private val redisRepositoryPort: RedisRepositoryPort,
    private val redisSyncPolicy: RedisSyncPolicy,
    private val cacheKeyGeneratorPort: CacheKeyGeneratorPort,
    private val cacheHitCounterPort: CacheHitCounterPort
) : CacheMovieUseCase {

    private val log = LoggerFactory.getLogger(CacheMovieService::class.java)

    /**
     * 영화 목록 조회 (캐시 O)
     * - Caffeine → Redis → DB 순서로 조회
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
        trySyncToRedis(cacheKey, loaded)

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

        redisRepositoryPort.getIfPresent(cacheKey).takeIf { it.isNotEmpty() }?.let {
            log.info("✅ [Redis HIT] key = $cacheKey")
            caffeineRepositoryPort.put(cacheKey, it)
            log.info("📥 [Caffeine PUT] (from Redis) key = $cacheKey")
            cacheHitCounterPort.increment(cacheKey)
            return it
        }

        log.info("⚠️ [Redis MISS] key = $cacheKey")
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

    /**
     * Redis 저장 (hit count 정책 기반)
     */
    private fun trySyncToRedis(cacheKey: String, responseList: List<QueryMovieResponse>) {
        val hitCount = cacheHitCounterPort.getHitCount(cacheKey)

        if (redisSyncPolicy.shouldSync(cacheKey, hitCount.toInt())) {
            redisRepositoryPort.put(cacheKey, responseList)
            log.info("📦 [Redis PUT] key = $cacheKey (hitCount = $hitCount)")
        } else {
            log.info("🚫 [Redis SKIP] key = $cacheKey (hitCount = $hitCount)")
        }
    }
}