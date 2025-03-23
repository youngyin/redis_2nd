package yin.adapter.out.persistence.repository

import yin.adapter.out.persistence.dto.ScheduleProjectionDto
import yin.application.command.QueryScheduleCommand

interface ScheduleCustom {
    fun findAllBySchedules(command: QueryScheduleCommand) : List<ScheduleProjectionDto>
}