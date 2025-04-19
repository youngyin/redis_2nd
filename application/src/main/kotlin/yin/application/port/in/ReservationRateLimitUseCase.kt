package yin.application.port.`in`

interface ReservationRateLimitUseCase {
    fun checkReservationLimit(userId: Long, scheduleId: Long, ttlSeconds: Long)
}