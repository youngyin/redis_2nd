package yin.adapter.out.persistence.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import yin.adapter.out.persistence.dto.ScheduleProjectionDto
import yin.adapter.out.persistence.entity.QMovieEntity.movieEntity
import yin.adapter.out.persistence.entity.QScheduleEntity.scheduleEntity
import yin.adapter.out.persistence.entity.QTheaterEntity.theaterEntity
import yin.application.command.QueryScheduleCommand

@Repository
class ScheduleCustomImpl(
    private val queryFactory: JPAQueryFactory
) : ScheduleCustom {
    override fun findAllBySchedules(command: QueryScheduleCommand): List<ScheduleProjectionDto> {
        return queryFactory
            .select(
                Projections.constructor(
                    ScheduleProjectionDto::class.java,
                    scheduleEntity.id,
                    scheduleEntity.movie.id,
                    theaterEntity.name,
                    scheduleEntity.startTime,
                    scheduleEntity.movie.runningTimeMin
                )
            )
            .from(scheduleEntity)
            .leftJoin(scheduleEntity.theater, theaterEntity)
            .leftJoin(scheduleEntity.movie, movieEntity)
            .where(scheduleEntity.movie.id.`in`(command.movieIds))
            .fetch()
    }
}
