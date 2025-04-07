package yin.adapter.`in`.controller.rateLimit

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import yin.adapter.`in`.controller.request.ReserveSeatRequest
import yin.application.command.ReserveSeatCommand
import yin.application.port.`in`.ReserveSeatUseCase
import yin.application.port.`in`.rateLimit.DsRateLimitReserveSeatUseCase
import yin.application.service.ReservationSeatService
import yin.domain.Reservation

@RestController
@RequestMapping("/api/v2/reservations")
class DsReservationController(
    private val reserveSeatUseCase: DsRateLimitReserveSeatUseCase
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
}