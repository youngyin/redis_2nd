package yin.adapter.`in`.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import yin.application.port.`in`.ReservationManagementUseCase
import yin.domain.Reservation

@RestController
@RequestMapping("/api/v1/reservations")
class ReservationManagementController(
    private val reservationManagementUseCase: ReservationManagementUseCase
) {
    /**
     * Retrieves all reservations.
     */
    @GetMapping("/me")
    fun getAllReservations(
        @RequestHeader("X-USER-ID") userId: Long,
    ): ResponseEntity<List<Reservation>> {
        val reservations = reservationManagementUseCase.getAllReservations(userId)
        return ResponseEntity.ok(reservations)
    }

    /**
     * Cancels a reservation for a given schedule.
     */
    @PutMapping("/{reservationId}/cancel")
    fun cancelReservation(
        @PathVariable("reservationId") reservationId: Long,
    ): ResponseEntity<Void> {
        reservationManagementUseCase.cancel(reservationId)
        return ResponseEntity.noContent().build()
    }

    /**
     * Confirms a reservation for a given schedule.
     */
    @PutMapping("/{reservationId}/confirm")
    fun confirmReservation(
        @PathVariable("reservationId") reservationId: Long,
    ): ResponseEntity<Void> {
        reservationManagementUseCase.confirm(reservationId)
        return ResponseEntity.noContent().build()
    }
}
