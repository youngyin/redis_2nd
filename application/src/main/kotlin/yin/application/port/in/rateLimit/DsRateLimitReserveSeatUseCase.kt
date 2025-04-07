package yin.application.port.`in`.rateLimit

import yin.application.command.ReserveSeatCommand
import yin.domain.Reservation

interface DsRateLimitReserveSeatUseCase {
    fun reserve(command: ReserveSeatCommand): Reservation
}