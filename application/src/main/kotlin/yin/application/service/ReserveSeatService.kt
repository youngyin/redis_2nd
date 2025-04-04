package yin.application.service

import org.springframework.stereotype.Service
import yin.application.command.ReserveSeatCommand
import yin.application.port.`in`.ReserveSeatUseCase
import yin.application.port.out.ReserveSeatPort
import yin.domain.Reservation
import yin.domain.ReservationStatus

@Service
class ReserveSeatService(
    private val reserveSeatPort: ReserveSeatPort
) : ReserveSeatUseCase {

    /**
     * Reserves a seat for a given user and schedule.
     *
     * @param command The reservation command containing user ID, schedule ID, and seat ID.
     * @return The reserved seat information.
     */
    override fun reserve(command: ReserveSeatCommand): Reservation {
        return reserveSeatPort.reserveSeat(
            Reservation(
                userId = command.userId,
                scheduleId = command.scheduleId,
                seatId = command.seatId,
                status = ReservationStatus.PENDING
            )
        )
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
    override fun confirm(id: Long) {
        reserveSeatPort.updateStatus(id, ReservationStatus.CANCELED)
    }
}