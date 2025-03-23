package yin.domain

data class Seat(
    val id: Long,
    val theaterId: Long,
    val seatRow: String,
    val seatNumber: Int
)