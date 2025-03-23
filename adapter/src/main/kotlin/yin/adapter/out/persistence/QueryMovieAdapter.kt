package yin.adapter.out.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import yin.adapter.out.persistence.repository.MovieRepository
import yin.adapter.out.persistence.repository.ScheduleRepository
import yin.application.command.QueryMovieCommand
import yin.application.command.QueryScheduleCommand
import yin.application.dto.QueryMovieResponse
import yin.application.dto.QueryScheduleResponse
import yin.application.port.out.MovieRepositoryPort

@Repository
class QueryMovieAdapter(
    private val movieRepository: MovieRepository,
    private val scheduleRepository: ScheduleRepository,
) : MovieRepositoryPort {

    override fun findAllMovies(command: QueryMovieCommand, pageable: Pageable): Page<QueryMovieResponse> {
        return movieRepository.findAllMovies(command, pageable).map {
            QueryMovieResponse(
                id = it.id,
                title = it.title,
                genre = it.genre,
                rating = it.rating,
                releaseDate = it.releaseDate,
                runningTime = it.runningTime,
                status = it.status,
            )
        }
    }

    override fun findAllSchedulesById(command: QueryScheduleCommand): List<QueryScheduleResponse> {
        return scheduleRepository.findAllBySchedules(command).map { schedule ->
            QueryScheduleResponse(
                id = schedule.id,
                movieId = schedule.movieId,
                theaterName = schedule.theaterName,
                startTime = schedule.startTime(),
                endTime = schedule.endTime()
            )
        }
    }
}