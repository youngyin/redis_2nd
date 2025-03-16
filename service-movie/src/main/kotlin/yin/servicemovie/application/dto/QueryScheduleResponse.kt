package yin.servicemovie.application.dto

import java.time.LocalTime

data class QueryScheduleResponse(
    val theaterName: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
)