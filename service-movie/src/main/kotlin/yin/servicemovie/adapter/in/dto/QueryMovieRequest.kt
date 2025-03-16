package yin.servicemovie.adapter.`in`.dto

import yin.servicemovie.domain.Genre
import yin.servicemovie.domain.MovieStatus

data class QueryMovieRequest(
    val title: String,
    val genreList: List<Genre> = listOf(),
    val movieStatusList: List<MovieStatus> = listOf(),
)