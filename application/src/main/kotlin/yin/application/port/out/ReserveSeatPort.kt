package yin.application.port.out

import yin.domain.Reservation
import yin.domain.ReservationStatus

interface ReserveSeatPort {
    fun reserveSeat(reservation: Reservation): Reservation
    fun getAllReservations(userId: Long): List<Reservation>
    fun updateStatus(id:Long, status: ReservationStatus)
}