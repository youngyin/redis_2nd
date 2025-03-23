package yin.application.port.`in`

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse

interface QueryMovieUseCase {
    fun findAllMovies(command: QueryMovieCommand, pageable: Pageable): Page<QueryMovieResponse>
}