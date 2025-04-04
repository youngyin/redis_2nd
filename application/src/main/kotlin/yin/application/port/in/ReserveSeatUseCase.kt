package yin.application.port.`in`

import yin.application.command.ReserveSeatCommand
import yin.domain.Reservation

interface ReserveSeatUseCase {
    fun reserve(command: ReserveSeatCommand): Reservation?
    fun getAllReservations(userId: Long): List<Reservation>
    fun cancel(id: Long)
    fun confirm(id: Long)
}