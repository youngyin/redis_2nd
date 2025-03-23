package yin.domain

import java.time.LocalDateTime

data class Schedule(
    val id: Long,
    val movieId: Long,
    val theaterId: Long,
    val startTime: LocalDateTime
)