package yin.adapter.`in`.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import yin.adapter.`in`.aop.DistributedLock
import yin.adapter.`in`.controller.request.ReserveSeatRequest
import yin.application.command.ReserveSeatCommand
import yin.application.port.`in`.SeatReservationUseCase
import yin.application.service.LocalLockReservationService
import yin.domain.Reservation

@RestController
@RequestMapping
class SeatReservationController(
    private val reserveSeatUseCase: SeatReservationUseCase,
    private val localLockReservationService: LocalLockReservationService
) {

    /**
     * Handles the reservation of a seat for a given schedule.
     */
    @PostMapping("/api/v1/reservations")
    fun reserveSeat(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: ReserveSeatRequest
    ): ResponseEntity<Reservation> {
        return handle(userId, request)
    }

    /**
     * Handles the reservation of a seat for a given schedule.
     * - Local lock is applied.
     */
    @PostMapping("/api/v2/reservations")
    fun reserveSeatLocalLock(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: ReserveSeatRequest
    ): ResponseEntity<Reservation> {
        val command = ReserveSeatCommand(
            userId = userId,
            scheduleId = request.scheduleId,
            seatId = request.seatId
        )
        val result = localLockReservationService.reserve(command)
        return ResponseEntity.ok(result)
    }

    /**
     * Handles the reservation of a seat for a given schedule.
     * - Distributed lock is applied.
     */
    @DistributedLock(
        key = "#seatId",
        waitTimeSeconds = 3,
    )
    @PostMapping("/api/v3/reservations")
    fun reserveSeatDistributedLock(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: ReserveSeatRequest
    ): ResponseEntity<Reservation> {
        return handle(userId, request)
    }

    /**
     * Handles the reservation of a seat for a given schedule.
     * - Late limit is applied.
     */
    @PostMapping("/api/v4/reservations")
    fun reserveSeatDs(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: ReserveSeatRequest
    ): ResponseEntity<Reservation> {
        return handle(userId, request, true)
    }

    private fun handle(
        userId: Long,
        request: ReserveSeatRequest,
        needLateLimit: Boolean = false
    ): ResponseEntity<Reservation> {
        val command = ReserveSeatCommand(
            userId = userId,
            scheduleId = request.scheduleId,
            seatId = request.seatId
        )
        val result = reserveSeatUseCase.reserve(command, needLateLimit)
        return ResponseEntity.ok(result)
    }
}