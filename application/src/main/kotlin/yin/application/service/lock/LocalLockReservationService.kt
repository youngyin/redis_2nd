package yin.application.service.lock

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.command.ReserveSeatCommand
import yin.application.port.`in`.Lock.LocalLockReservationUseCase
import yin.application.port.out.ReserveSeatPort
import yin.domain.Reservation
import yin.domain.ReservationStatus

@Transactional(readOnly = true)
@Service
class LocalLockReservationService(
    private val reserveSeatPort: ReserveSeatPort,

) : LocalLockReservationUseCase{
    /**
     * Reserves a seat for a given user and schedule.
     */
    @Transactional
    override fun reserve(command: ReserveSeatCommand): Reservation {
        if (reserveSeatPort.existsByScheduleIdAndSeatId_Lock(command.scheduleId, command.seatId)) {
            throw IllegalStateException("이미 예약된 좌석입니다.")
        }

        val reservation = Reservation(
            userId = command.userId,
            scheduleId = command.scheduleId,
            seatId = command.seatId,
            status = ReservationStatus.PENDING
        )

        return reserveSeatPort.reserveSeat(reservation)
    }
}