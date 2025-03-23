package yin.adapter.`in`.request

import yin.domain.Genre
import yin.domain.MovieStatus

data class QueryMovieRequest(
    val title: String,
    val genreList: List<Genre> = listOf(),
    val movieStatusList: List<MovieStatus> = listOf(),
)