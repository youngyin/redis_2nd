package yin.servicemovie.adapter.out.persistence

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import yin.servicemovie.adapter.out.persistence.entity.QImageEntity.imageEntity
import yin.servicemovie.adapter.out.persistence.entity.QMovieEntity.movieEntity
import yin.servicemovie.adapter.out.persistence.entity.QScheduleEntity.scheduleEntity
import yin.servicemovie.adapter.out.persistence.entity.QTheaterEntity.theaterEntity
import yin.servicemovie.application.command.QueryMovieCommand
import yin.servicemovie.application.dto.QueryMovieResponse
import yin.servicemovie.domain.Genre

class MovieCustomImpl(
    private val queryFactory: JPAQueryFactory
) : MovieCustom {

    override fun findAllMovies(command: QueryMovieCommand, pageable: Pageable): Page<QueryMovieResponse> {
        val query = queryFactory
            .select(
                Projections.constructor(
                    QueryMovieResponse::class.java,
                    movieEntity.id,
                    movieEntity.title,
                    movieEntity.genre,
                    movieEntity.releaseDate,
                    movieEntity.runningTime,
                    movieEntity.status,
                    imageEntity.url,
                    scheduleEntity.startTime,
                    theaterEntity.name
                )
            )
            .from(movieEntity)
            .leftJoin(imageEntity).on(imageEntity.movie.eq(movieEntity))  // 이미지와 조인
            .leftJoin(scheduleEntity).on(scheduleEntity.movie.eq(movieEntity))  // 일정과 조인
            .leftJoin(theaterEntity).on(scheduleEntity.theater.eq(theaterEntity))  // 극장과 조인
            .where(
                genreEqIn(command.genreList),
                titleContains(command.title)
            )

        // 페이징 처리
        val totalCount = queryFactory
            .select(movieEntity.count())
            .from(movieEntity)
            .leftJoin(imageEntity).on(imageEntity.movie.eq(movieEntity))
            .leftJoin(scheduleEntity).on(scheduleEntity.movie.eq(movieEntity))
            .leftJoin(theaterEntity).on(scheduleEntity.theater.eq(theaterEntity))
            .where(
                genreEqIn(command.genreList),
                titleContains(command.title)
            )
            .fetchOne() ?: 0L

        val results = query.offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(results, pageable, totalCount)
    }

    // 장르 필터링
    private fun genreEqIn(genreList: List<Genre>?): BooleanExpression? {
        return genreList?.takeIf { it.isNotEmpty() }?.let {
            movieEntity.genre.`in`(it)
        }
    }

    // 제목 검색 필터링
    private fun titleContains(title: String?): BooleanExpression? {
        return title?.let { movieEntity.title.containsIgnoreCase(it) }
    }
}