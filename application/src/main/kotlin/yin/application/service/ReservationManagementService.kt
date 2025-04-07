package yin.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.port.`in`.ReservationManagementUseCase
import yin.application.port.out.ReserveSeatPort
import yin.domain.Reservation
import yin.domain.ReservationStatus

@Transactional(readOnly = true)
@Service
class ReservationManagementService(
    private val reserveSeatPort: ReserveSeatPort,
) : ReservationManagementUseCase {

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