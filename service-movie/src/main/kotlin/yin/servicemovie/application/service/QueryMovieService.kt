package yin.servicemovie.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import yin.servicemovie.application.command.QueryMovieCommand
import yin.servicemovie.application.dto.QueryMovieResponse
import yin.servicemovie.port.`in`.QueryMovieUseCase
import yin.servicemovie.port.out.MovieRepositoryPort

@Service
class QueryMovieService(
    private val movieRepositoryPort: MovieRepositoryPort
) : QueryMovieUseCase{

    override fun findAllMovies(
        command: QueryMovieCommand,
        pageable: Pageable
    ): Page<QueryMovieResponse> {
        return movieRepositoryPort.findAllMovies(command, pageable)
    }
}