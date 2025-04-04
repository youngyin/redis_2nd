package yin.adapter.out.persistence

import org.springframework.stereotype.Component
import yin.application.port.out.ReserveSeatPort
import yin.domain.Reservation
import yin.adapter.out.persistence.repository.ReservationRepository
import yin.adapter.out.persistence.entity.ReservationEntity
import yin.adapter.out.persistence.repository.ScheduleRepository
import yin.adapter.out.persistence.repository.SeatRepository
import yin.adapter.out.persistence.repository.UserRepository
import yin.domain.ReservationStatus

@Component
class ReserveSeatPersistenceAdapter(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val scheduleRepository: ScheduleRepository,
    private val seatRepository: SeatRepository,
) : ReserveSeatPort {

    /**
     * Checks if a seat is already reserved for a given schedule.
     *
     * @param scheduleId The ID of the schedule.
     * @param seatId The ID of the seat.
     * @return True if the seat is reserved, false otherwise.
     */
    override fun existsByScheduleIdAndSeatId(scheduleId: Long, seatId: Long): Boolean {
        return reservationRepository.existsByScheduleIdAndSeatId(scheduleId, seatId)
    }

    /**
     * Reserves a seat for a given user and schedule.
     *
     * @param reservation The reservation details.
     * @return The reserved seat information.
     */
    override fun reserveSeat(reservation: Reservation): Reservation {
        val userEntity = userRepository.findById(reservation.userId)
            .orElseThrow({ IllegalArgumentException("User not found") })
        val scheduleEntity = scheduleRepository.findById(reservation.scheduleId)
            .orElseThrow({ IllegalArgumentException("Schedule not found") })
        val seatEntity = seatRepository.findById(reservation.seatId)
            .orElseThrow { IllegalArgumentException("Seat ${reservation.seatId} not found") }

        val saved = reservationRepository.save(
            ReservationEntity(
            user = userEntity,
            schedule = scheduleEntity,
            seat = seatEntity,
            status = reservation.status
        ))

        return Reservation(
            id = saved.id!!,
            userId = userEntity.id,
            scheduleId = scheduleEntity.id,
            seatId = seatEntity.id,
            status = saved.status
        )
    }

    /**
     * Retrieves all reservations for a given user.
     *
     * @param userId The ID of the user.
     * @return A list of reservations for the user.
     */
    override fun getAllReservations(userId: Long): List<Reservation> {
        reservationRepository.findAllByUserId(userId)
            .map { reservation ->
                Reservation(
                    id = reservation.id!!,
                    userId = reservation.user.id,
                    scheduleId = reservation.schedule.id,
                    seatId = reservation.seat.id,
                    status = reservation.status
                )
            }.let { reservations ->
                return reservations
            }
    }

    /**
     * Updates the status of a reservation.
     *
     * @param id The ID of the reservation.
     * @param status The new status of the reservation.
     */
    override fun updateStatus(id: Long, status: ReservationStatus) {
        val reservationEntity = reservationRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Reservation not found") }
        reservationEntity.status = status
    }
}
