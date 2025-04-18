package yin.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.command.ReserveSeatCommand
import yin.application.port.`in`.LocalLockReservationUseCase
import yin.application.port.out.ReserveSeatPort
import yin.application.port.out.SeatRepositoryPort
import yin.domain.Reservation
import yin.domain.ReservationStatus

@Transactional(readOnly = true)
@Service
class LocalLockReservationService(
    private val reserveSeatPort: ReserveSeatPort,
    private val seatRepository: SeatRepositoryPort

) : LocalLockReservationUseCase {
    /**
     * Reserves a seat for a given user and schedule.
     */
    @Transactional
    override fun reserve(command: ReserveSeatCommand): Reservation {
        val seat = seatRepository.findByIdWithLock(command.seatId)
        val alreadyReserved = reserveSeatPort.existsByScheduleIdAndSeatId(command.scheduleId, command.seatId)

        if (alreadyReserved) {
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