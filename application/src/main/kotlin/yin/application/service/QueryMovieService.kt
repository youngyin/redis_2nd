package yin.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse
import yin.application.port.`in`.QueryMovieUseCase
import yin.application.port.out.MovieRepositoryPort

@Transactional(readOnly = true)
@Service
class QueryMovieService(
    private val movieRepositoryPort: MovieRepositoryPort
) : QueryMovieUseCase {

    override fun findAllMovies(
        command: QueryMovieCommand,
        pageable: Pageable
    ): Page<QueryMovieResponse> {
        return movieRepositoryPort.findAllMovies(command, pageable)
    }
}