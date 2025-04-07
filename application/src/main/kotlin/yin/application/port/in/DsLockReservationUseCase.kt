package yin.application.port.`in`

import yin.application.command.ReserveSeatCommand
import yin.domain.Reservation

@Deprecated("AOP기반의 분산락 적용")
interface DsLockReservationUseCase {
    fun reserve(command: ReserveSeatCommand): Reservation?
}