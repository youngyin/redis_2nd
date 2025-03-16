package yin.servicemovie.port.out

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import yin.servicemovie.application.command.QueryMovieCommand
import yin.servicemovie.application.dto.QueryMovieResponse

interface MovieRepositoryPort {
    fun findAllMovies(command: QueryMovieCommand, pageable: Pageable): Page<QueryMovieResponse>
}