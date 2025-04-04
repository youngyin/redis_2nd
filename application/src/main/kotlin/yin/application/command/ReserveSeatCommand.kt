package yin.application.command

data class ReserveSeatCommand(
    val userId: Long,
    val scheduleId: Long,
    val seatId: Long
)