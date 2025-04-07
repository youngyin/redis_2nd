package yin.application.port.`in`

import yin.application.command.ReserveSeatCommand
import yin.domain.Reservation

interface LocalLockReservationUseCase {
    fun reserve(command: ReserveSeatCommand): Reservation?
}