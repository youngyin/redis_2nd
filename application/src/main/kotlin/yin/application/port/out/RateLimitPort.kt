package yin.application.port.out

interface RateLimitPort {
    fun checkIpRequestLimit(ip: String?)
    fun checkReservationLimit(userId: Long, scheduleId: Long, ttlSeconds: Long)
}