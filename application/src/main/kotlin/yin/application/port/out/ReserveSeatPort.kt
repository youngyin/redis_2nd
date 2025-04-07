package yin.application.port.out

import yin.domain.Reservation
import yin.domain.ReservationStatus

interface ReserveSeatPort {
    fun existsByScheduleIdAndSeatId(scheduleId: Long, seatId: Long): Boolean
    fun reserveSeat(reservation: Reservation): Reservation
    fun getAllReservations(userId: Long): List<Reservation>
    fun updateStatus(id:Long, status: ReservationStatus)
    fun existsByScheduleIdAndSeatId_Lock(scheduleId: Long, seatId: Long): Boolean
}