package yin.application.dto

import java.time.LocalTime

data class QueryScheduleResponse(
    val id: Long,
    val movieId : Long,
    val theaterName: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
)