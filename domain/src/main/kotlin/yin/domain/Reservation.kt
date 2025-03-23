package yin.domain

data class Reservation(
    val id: Long,
    val userId: Long,
    val scheduleId: Long,
    val seatId: Long,
    val status: String
)