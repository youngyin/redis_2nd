package yin.application.port.`in`

import yin.application.command.ReserveSeatCommand
import yin.domain.Reservation

interface SeatReservationUseCase {
    fun reserve(command: ReserveSeatCommand, needLateLimit: Boolean = false): Reservation
}