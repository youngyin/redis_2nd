package yin.adapter.`in`.controller.cache

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import yin.adapter.`in`.controller.request.QueryMovieRequest
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse
import yin.application.port.`in`.cache.DsCacheMovieUseCase

@RestController
@RequestMapping("/api/v2/movies")
class DsCacheMovieController(
    private val dsCacheMovieUseCase: DsCacheMovieUseCase
) {

    /**
     * 영화 목록 조회 (캐시 O)
     */
    @GetMapping
    fun getMoviesWithCache(
        @Validated request: QueryMovieRequest,
        pageable: Pageable
    ): ResponseEntity<Page<QueryMovieResponse>> {
        val command = QueryMovieCommand(
            title = request.title,
            genreList = request.genreList,
            movieStatusList = request.movieStatusList,
        )
        val findAllMovies = dsCacheMovieUseCase.findAllMovies(command, pageable)
        return ResponseEntity.ok().body(findAllMovies)
    }
}