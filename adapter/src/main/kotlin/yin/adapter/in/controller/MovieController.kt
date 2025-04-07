package yin.adapter.`in`.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import yin.adapter.`in`.aop.Cached3Layer
import yin.adapter.`in`.aop.CachedLocal
import yin.adapter.`in`.controller.request.QueryMovieRequest
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse
import yin.application.port.`in`.QueryMovieUseCase

@RestController
@RequestMapping("/api")
class MovieController(
    private val queryMovieUseCase: QueryMovieUseCase
) {

    /**
     * 영화 목록 조회 (캐시 X)
     */
    @GetMapping("/v1/movies")
    fun getMovies(
        @Validated request: QueryMovieRequest,
        pageable: Pageable
    ): ResponseEntity<Page<QueryMovieResponse>> {
        return handle(request, pageable)
    }

    /**
     * 영화 목록 조회 (캐시 O)
     */
    @Cached3Layer(
        cacheKeyPrefix = "movie",
        ttlSeconds = 300,
        syncThreshold = 5  // Redis로 승격할 최소 hit count
    )
    @GetMapping("/v2/movies")
    fun getMoviesDsCache(
        @Validated request: QueryMovieRequest,
        pageable: Pageable
    ): ResponseEntity<Page<QueryMovieResponse>> {
        return handle(request, pageable)
    }

    /**
     * 영화 목록 조회 (캐시 O)
     */
    @CachedLocal(
        cacheKeyPrefix = "movie",
        ttlSeconds = 300,
    )
    @GetMapping("/v3/movies")
    fun getMoviesLocalCache(
        @Validated request: QueryMovieRequest,
        pageable: Pageable
    ): ResponseEntity<Page<QueryMovieResponse>> {
        return handle(request, pageable)
    }

    private fun handle(
        request: QueryMovieRequest,
        pageable: Pageable
    ): ResponseEntity<Page<QueryMovieResponse>> {
        val command = QueryMovieCommand(
            title = request.title,
            genreList = request.genreList,
            movieStatusList = request.movieStatusList,
        )
        val findAllMovies = queryMovieUseCase.findAllMovies(command, pageable)
        return ResponseEntity.ok().body(findAllMovies)
    }
}