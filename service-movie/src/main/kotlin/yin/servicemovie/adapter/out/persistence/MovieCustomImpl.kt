package yin.servicemovie.adapter.out.persistence

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import yin.servicemovie.application.command.QueryMovieCommand
import yin.servicemovie.domain.Genre
import yin.servicemovie.application.dto.QueryMovieResponse

class MovieCustomImpl(
    private val queryFactory: JPAQueryFactory
) : MovieCustom {
    override fun findAllMovies(command: QueryMovieCommand, pageable: Pageable): Page<QueryMovieResponse> {
//        val query = queryFactory
//            .select(
//                Projections.constructor(
//                    QueryMovieResponse::class.java,
//                    movie.id,
//                    movie.title,
//                    movie.genre,
//                    movie.rating,
//                    movie.releaseDate,
//                    movie.runningTime,
//                    movie.status,
//                    movieImage.url,
//                    schedule.startTime,
//                    theater.name
//                )
//            )
//            .from(movie)
//            .leftJoin(movieImage).on(movie.id.eq(movieImage.movie.id))
//            .leftJoin(schedule).on(movie.id.eq(schedule.movie.id))
//            .leftJoin(theater).on(schedule.theater.id.eq(theater.id))
//            .where(
//                genreEq(command.genre),
//                titleContains(command.title)
//            )
//
//        val result = querydsl!!.applyPagination(pageable, query).fetch()
//        val countQuery = queryFactory.select(movie.count()).from(movie)
//
//        return PageImpl(result, pageable, countQuery.fetchOne() ?: 0L)
//    }
//
//    private fun genreEq(genre: Genre?): BooleanExpression? {
//        return genre?.let { movie.genre.eq(it) }
//    }
//
//    private fun titleContains(title: String?): BooleanExpression? {
//        return title?.let { movie.title.containsIgnoreCase(it) }

        return Page.empty()
    }
}
