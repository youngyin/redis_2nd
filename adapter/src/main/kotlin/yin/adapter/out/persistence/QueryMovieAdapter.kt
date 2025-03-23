package yin.adapter.out.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import yin.adapter.out.persistence.repository.MovieRepository
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse
import yin.application.port.out.MovieRepositoryPort

@Repository
class QueryMovieAdapter(
    private val movieRepository: MovieRepository
) : MovieRepositoryPort {
    override fun findAllMovies(command: QueryMovieCommand, pageable: Pageable): Page<QueryMovieResponse> {
        return movieRepository.findAllMovies(command, pageable)
    }
}