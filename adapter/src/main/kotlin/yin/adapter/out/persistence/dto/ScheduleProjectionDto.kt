package yin.adapter.out.persistence.dto

import java.time.LocalDateTime
import java.time.LocalTime

data class ScheduleProjectionDto(
    val id: Long,
    val movieId: Long,
    val theaterName: String,
    val startDateTime: LocalDateTime,
    val runningTimeMin: Int
) {
    fun startTime(): LocalTime = startDateTime.toLocalTime()
    fun endTime(): LocalTime = startDateTime.toLocalTime().plusMinutes(runningTimeMin.toLong())
}
