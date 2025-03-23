package yin.application.port.out

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse

interface MovieRepositoryPort {
    fun findAllMovies(command: QueryMovieCommand, pageable: Pageable): Page<QueryMovieResponse>
}