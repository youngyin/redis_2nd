package yin.adapter.out.persistence.dto

import yin.domain.Genre
import yin.domain.MovieStatus
import java.time.LocalDate

data class MovieProjectionDto(
    val id: Long,
    val title: String,
    val genre: Genre,
    val rating: String,
    val releaseDate: LocalDate,
    val runningTime: Int,
    val status: MovieStatus,
    val imageUrl: String?
)
