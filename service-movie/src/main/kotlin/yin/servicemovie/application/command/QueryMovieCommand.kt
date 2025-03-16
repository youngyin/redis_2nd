package yin.servicemovie.application.command

import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import yin.servicemovie.adapter.`in`.dto.QueryMovieRequest
import yin.servicemovie.domain.Genre
import yin.servicemovie.domain.MovieStatus

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
data class QueryMovieCommand(
    val title: String,
    val genreList: List<Genre> = listOf(),
    val movieStatusList: List<MovieStatus> = listOf(),
){
    constructor(request: QueryMovieRequest) : this(
        request.title,
        request.genreList,
        request.movieStatusList
    )
}
