package yin.application.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse
import yin.application.port.`in`.CacheMovieUseCase
import yin.application.port.out.CaffeineRepositoryPort
import yin.application.port.out.MovieRepositoryPort
import yin.application.port.out.RedisRepositoryPort
import yin.domain.Genre
import java.security.MessageDigest

@Service
class CacheMovieService(
    private val movieRepositoryPort: MovieRepositoryPort,
    private val caffeineRepositoryPort: CaffeineRepositoryPort,
    private val redisRepositoryPort: RedisRepositoryPort
) : CacheMovieUseCase {

    private val log = LoggerFactory.getLogger(CacheMovieService::class.java)

    override fun findAllMovies(
        command: QueryMovieCommand,
        pageable: Pageable
    ): Page<QueryMovieResponse> {

        // ① 캐시 키 생성
        val cacheKey = if (command.title.isNullOrBlank() && command.genreList.isEmpty()) {
            "movies:all"
        } else {
            val genreHash = genreListHash(command.genreList)
            "movies:search:${command.title}:${genreHash}"
        }

        // ② Caffeine 조회
        val cachedFromCaffeine = caffeineRepositoryPort.getIfPresent(cacheKey)
        if (cachedFromCaffeine.isNotEmpty()) {
            log.info("✅ [Caffeine HIT] key = $cacheKey")
            return cachedFromCaffeine.toPage(pageable)
        }
        log.info("⚠️ [Caffeine MISS] key = $cacheKey")

        // ③ Redis 조회
        val cachedFromRedis = redisRepositoryPort.getIfPresent(cacheKey)
        if (cachedFromRedis.isNotEmpty()) {
            log.info("✅ [Redis HIT] key = $cacheKey")
            caffeineRepositoryPort.put(cacheKey, cachedFromRedis)
            log.info("📥 [Caffeine PUT] (from Redis) key = $cacheKey")
            return cachedFromRedis.toPage(pageable)
        }
        log.info("⚠️ [Redis MISS] key = $cacheKey")

        // ④ DB 조회
        log.info("📡 [DB QUERY] key = $cacheKey")
        val movieList = movieRepositoryPort.findAllMovies(command, pageable)
        val movieIds = movieList.content.map { it.id }
        val schedules = movieRepositoryPort.findSchedulesByMovieIdIn(movieIds)
        val scheduleMap = schedules.groupBy { it.movieId }

        val responseList = movieList.content.map { movie ->
            val movieSchedules = scheduleMap[movie.id] ?: emptyList()
            movie.copy(schedules = movieSchedules)
        }

        // ⑤ 캐시 저장
        caffeineRepositoryPort.put(cacheKey, responseList)
        redisRepositoryPort.put(cacheKey, responseList)
        log.info("📥 [Caffeine PUT] (from DB) key = $cacheKey")
        log.info("📦 [Redis PUT] key = $cacheKey")

        return responseList.toPage(pageable)
    }

    private fun List<QueryMovieResponse>.toPage(pageable: Pageable): Page<QueryMovieResponse> {
        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(this.size)
        val content = this.subList(start, end)
        return PageImpl(content, pageable, this.size.toLong())
    }

    fun genreListHash(genreList: List<Genre>): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(genreList.sorted().joinToString(",").toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}