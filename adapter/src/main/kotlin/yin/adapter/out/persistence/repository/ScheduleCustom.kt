package yin.adapter.out.persistence.repository

import yin.adapter.out.persistence.dto.ScheduleProjectionDto
import yin.application.command.QueryScheduleCommand

interface ScheduleCustom {
    fun findByMovieIdIn(movieIds: List<Long>) : List<ScheduleProjectionDto>
}