package yin.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.command.QueryMovieCommand
import yin.application.command.QueryScheduleCommand
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
        val moviesPage = movieRepositoryPort.findAllMovies(command, pageable)
        val movieIds = moviesPage.content.map { it.id.toLong() }

        /**
         * movie : schedule 은 1:N 관계이므로, 따로 조회해서 조립해야 함.
         * 같이 join해서 조립하면 schedule 의 개수만큼 중복된 데이터를 얻을수 있다.
         */
        val schedules = movieRepositoryPort.findAllSchedulesById(QueryScheduleCommand(movieIds))
        val scheduleMap = schedules.groupBy { it.movieId }

        return moviesPage.map { movie ->
            val movieSchedules = scheduleMap[movie.id] ?: emptyList()
            movie.copy(
                schedules = movieSchedules
            )
        }
    }
}
