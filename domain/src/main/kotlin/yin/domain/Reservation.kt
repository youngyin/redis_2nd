package yin.domain

data class Reservation(
    val id: Long? = null,
    val userId: Long,
    val scheduleId: Long,
    val seatId: Long,
    val status: ReservationStatus
)