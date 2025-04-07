package yin.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.command.ReserveSeatCommand
import yin.application.port.`in`.ReserveSeatUseCase
import yin.application.port.out.ReserveSeatPort
import yin.domain.Reservation
import yin.domain.ReservationStatus

@Transactional(readOnly = true)
@Service
class ReservationSeatService(
    private val reserveSeatPort: ReserveSeatPort,
) : ReserveSeatUseCase {

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

        return reserveSeatPort.reserveSeat(reservation)
    }

    /**
     * Retrieves all reservations for a given user.
     *
     * @param userId The ID of the user.
     * @return A list of reservations for the user.
     */
    override fun getAllReservations(userId: Long): List<Reservation> {
        return reserveSeatPort.getAllReservations(userId)
    }

    /**
     * Cancels a reservation for a given user, schedule, and seat.
     *
     * @param userId The ID of the user.
     * @param scheduleId The ID of the schedule.
     * @param seatId The ID of the seat.
     */
    @Transactional
    override fun cancel(id: Long) {
        reserveSeatPort.updateStatus(id, ReservationStatus.CANCELED)
    }

    /**
     * Confirms a reservation for a given user, schedule, and seat.
     *
     * @param userId The ID of the user.
     * @param scheduleId The ID of the schedule.
     * @param seatId The ID of the seat.
     */
    @Transactional
    override fun confirm(id: Long) {
        reserveSeatPort.updateStatus(id, ReservationStatus.CONFIRMED)
    }
}