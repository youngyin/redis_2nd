package yin.adapter.`in`.controller

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
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
    private val logger = LoggerFactory.getLogger(javaClass)

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
    @Transactional
    @DistributedLock(
        key = "#request.seatId",
        waitTimeSeconds = 10,
        timeoutSeconds = 30
    )
    @PostMapping("/api/v3/reservations")
    fun reserveSeatDistributedLock(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: ReserveSeatRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Reservation> {
        val responseEntity = handle(userId, request)
        val serverPort = servletRequest.localPort
        logger.info("[reservation complete] 서버 포트: $serverPort, 유저 ID: $userId, 스케줄 ID: ${request.scheduleId}, 좌석 ID: ${request.seatId}, 예약 시간 : ${System.currentTimeMillis()}")
        return responseEntity
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