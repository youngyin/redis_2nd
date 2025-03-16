package yin.servicemovie.domain

import java.time.LocalDate

data class Movie(
    val id: Long,
    val title: String,
    val genre: Genre,
    val releaseDate: LocalDate,
    val runningTime: Int,
    val status: MovieStatus
)
