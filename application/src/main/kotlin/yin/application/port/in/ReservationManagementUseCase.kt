package yin.application.port.`in`

import yin.domain.Reservation

interface ReservationManagementUseCase {
    fun getAllReservations(userId: Long): List<Reservation>
    fun cancel(id: Long)
    fun confirm(id: Long)
}