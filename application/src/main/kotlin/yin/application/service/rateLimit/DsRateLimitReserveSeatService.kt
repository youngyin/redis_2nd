package yin.application.service.rateLimit

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.command.ReserveSeatCommand
import yin.application.port.`in`.rateLimit.DsRateLimitReserveSeatUseCase
import yin.application.port.out.RateLimitPort
import yin.application.port.out.ReserveSeatPort
import yin.domain.Reservation
import yin.domain.ReservationStatus

@Transactional(readOnly = true)
@Service
class DsRateLimitReserveSeatService(
    private val reserveSeatPort: ReserveSeatPort,
    private val rateLimitPort: RateLimitPort,
) : DsRateLimitReserveSeatUseCase {

    /**
     * Reserves a seat for a given schedule.
     */
    @Transactional
    override fun reserve(command: ReserveSeatCommand): Reservation {
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

        rateLimitPort.checkReservationLimit(command.userId, command.scheduleId, 300L  )// 5분

        return reserveSeat
    }
}