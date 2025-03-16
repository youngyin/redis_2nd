package yin.servicemovie.adapter.out.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import yin.servicemovie.application.command.QueryMovieCommand
import yin.servicemovie.application.dto.QueryMovieResponse
import yin.servicemovie.port.out.MovieRepositoryPort

@Repository
class QueryMovieAdapter(
    private val movieRepository: MovieRepository
) : MovieRepositoryPort {
    override fun findAllMovies(command: QueryMovieCommand, pageable: Pageable): Page<QueryMovieResponse> {
        return movieRepository.findAllMovies(command, pageable)
    }
}