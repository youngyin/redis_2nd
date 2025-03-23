package yin.application.command

import yin.domain.Genre
import yin.domain.MovieStatus

data class QueryMovieCommand(
    val title: String?,
    val genreList: List<Genre> = listOf(),
    val movieStatusList: List<MovieStatus> = listOf(),
)