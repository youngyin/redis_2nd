package yin.adapter.`in`.controller.rateLimit

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import yin.adapter.`in`.aop.RateLimited
import yin.adapter.`in`.controller.request.QueryMovieRequest
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse
import yin.application.port.`in`.QueryMovieUseCase

@RestController
@RequestMapping("/api/v4/movies")
class DsReservationMovieController(
    private val queryMovieUseCase: QueryMovieUseCase
) {

    /**
     * 영화 목록 조회 (캐시 X)
     */
    @RateLimited
    @GetMapping
    fun getMovies(
        @Validated request: QueryMovieRequest,
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