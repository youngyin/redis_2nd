package yin.servicemovie.adapter.`in`

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import yin.servicemovie.adapter.`in`.dto.QueryMovieRequest
import yin.servicemovie.application.command.QueryMovieCommand
import yin.servicemovie.application.dto.QueryMovieResponse
import yin.servicemovie.port.`in`.QueryMovieUseCase

@RestController
@RequestMapping("/api/v1/movies")
class QueryMovieController(
    private val queryMovieUseCase: QueryMovieUseCase
) {
    @GetMapping
    fun getMovies(request: QueryMovieRequest, pageable: Pageable): ResponseEntity<Page<QueryMovieResponse>> {
        val command = QueryMovieCommand(request)
        val findAllMovies = queryMovieUseCase.findAllMovies(command, pageable)
        return ResponseEntity.ok().body(findAllMovies)
    }
}