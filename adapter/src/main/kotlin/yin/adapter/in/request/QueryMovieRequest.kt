package yin.adapter.`in`.request

import jakarta.validation.constraints.Size
import yin.domain.Genre
import yin.domain.MovieStatus

data class QueryMovieRequest(
    @field:Size(max = 255, message = "제목은 255자를 넘을 수 없습니다.")
    val title: String? = null,

    val genreList: List<Genre> = listOf(),

    val movieStatusList: List<MovieStatus> = listOf()
)
