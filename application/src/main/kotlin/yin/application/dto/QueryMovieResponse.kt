package yin.application.dto

import yin.domain.MovieStatus
import java.time.LocalDate

data class QueryMovieResponse(
    val id: String,
    val title: String,
    val genre: String,
    val rating: String,
    val releaseDate: LocalDate,
    val runningTime: Int,
    val status: MovieStatus,
    val schedules: List<QueryScheduleResponse>
)