package yin.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.command.ReserveSeatCommand
import yin.application.port.`in`.SeatReservationUseCase
import yin.application.port.out.ReserveSeatPort
import yin.domain.Reservation
import yin.domain.ReservationStatus

@Transactional(readOnly = true)
@Service
class SeatReservationService(
    private val reserveSeatPort: ReserveSeatPort,
) : SeatReservationUseCase {

    /**
     * Reserves a seat for a given schedule.
     */
    @Transactional
    override fun reserve(command: ReserveSeatCommand): Reservation {
        // 예약 가능 여부 체크
        if (reserveSeatPort.existsByScheduleIdAndSeatId(command.scheduleId, command.seatId)) {
            throw IllegalStateException("이미 예약된 좌석입니다.")
        }

        val reservation = Reservation(
            userId = command.userId,
            scheduleId = command.scheduleId,
            seatId = command.seatId,
            status = ReservationStatus.PENDING
        )

        val reserveSeat = reserveSeatPort.reserveSeat(reservation)


        return reserveSeat
    }
}