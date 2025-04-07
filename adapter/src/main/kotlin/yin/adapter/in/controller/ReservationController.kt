package yin.adapter.`in`.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import yin.adapter.`in`.controller.request.ReserveSeatRequest
import yin.application.command.ReserveSeatCommand
import yin.application.port.`in`.ReserveSeatUseCase
import yin.domain.Reservation

@RestController
@RequestMapping("/api/v1/reservations")
class ReservationController(
    private val reserveSeatUseCase: ReserveSeatUseCase
) {

    /**
     * Handles the reservation of a seat for a given schedule.
     */
    @PostMapping
    fun reserveSeat(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: ReserveSeatRequest
    ): ResponseEntity<Reservation> {
        val command = ReserveSeatCommand(
            userId = userId,
            scheduleId = request.scheduleId,
            seatId = request.seatId
        )
        val result = reserveSeatUseCase.reserve(command)
        return ResponseEntity.ok(result)
    }

    /**
     * Retrieves all reservations.
     */
    @GetMapping("/me")
    fun getAllReservations(
        @RequestHeader("X-USER-ID") userId: Long,
    ): ResponseEntity<List<Reservation>> {
        val reservations = reserveSeatUseCase.getAllReservations(userId)
        return ResponseEntity.ok(reservations)
    }

    /**
     * Cancels a reservation for a given schedule.
     */
    @PutMapping("/{reservationId}/cancel")
    fun cancelReservation(
        @PathVariable("reservationId") reservationId: Long,
    ): ResponseEntity<Void> {
        reserveSeatUseCase.cancel(reservationId)
        return ResponseEntity.noContent().build()
    }

    /**
     * Confirms a reservation for a given schedule.
     */
    @PutMapping("/{reservationId}/confirm")
    fun confirmReservation(
        @PathVariable("reservationId") reservationId: Long,
    ): ResponseEntity<Void> {
        reserveSeatUseCase.confirm(reservationId)
        return ResponseEntity.noContent().build()
    }
}
