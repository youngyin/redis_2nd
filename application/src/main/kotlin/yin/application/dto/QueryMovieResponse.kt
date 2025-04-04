package yin.application.dto

import yin.domain.Genre
import yin.domain.MovieStatus
import java.time.LocalDate


data class QueryMovieResponse(
    val id: Long,
    val title: String,
    val genre: Genre,
    val rating: String,
    val releaseDate: LocalDate,
    val runningTime: Int,
    val status: MovieStatus,
    val schedules: List<QueryScheduleResponse> = listOf(),
)