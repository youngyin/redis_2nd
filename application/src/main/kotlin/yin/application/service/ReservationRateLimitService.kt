package yin.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import yin.application.port.`in`.ReservationRateLimitUseCase
import yin.application.port.out.RateLimitPort

@Service
class ReservationRateLimitService(
    private val rateLimitPort: RateLimitPort
) : ReservationRateLimitUseCase {
    private val logger = LoggerFactory.getLogger(ReservationRateLimitService::class.java)

    /**
     * Checks the reservation limit for a user and schedule.
     */
    override fun checkReservationLimit(userId: Long, scheduleId: Long, ttlSeconds: Long) {
        logger.info("Checking reservation limit for userId: $userId, scheduleId: $scheduleId, ttlSeconds: $ttlSeconds")
        rateLimitPort.checkReservationLimit(userId, scheduleId, ttlSeconds)
    }

}