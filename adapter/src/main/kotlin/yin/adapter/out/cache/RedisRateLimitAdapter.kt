package yin.adapter.out.cache

import org.redisson.api.RScript
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import yin.application.port.out.RateLimitPort
import yin.infrastructure.error.RateLimitException

@Service
class RedisRateLimitAdapter(
    private val redissonClient: RedissonClient
) : RateLimitPort {

    /**
     * IP 요청 제한 체크
     */
    override fun checkIpRequestLimit(ip: String) {
        val keys: List<Any> = listOf(
            "ratelimit:ip:$ip:minute",
            "ratelimit:ip:$ip:blocked"
        )

        val result = redissonClient.script.eval<Long>(
            RScript.Mode.READ_WRITE,
            loadLua("ip_ratelimit.lua"),
            RScript.ReturnType.INTEGER,
            keys,
        )
        if (result == 0L) throw RateLimitException("요청이 너무 많습니다. 1시간 후 다시 시도해주세요.")
    }

    /**
     * 예약 요청 제한 체크
     */
    override fun checkReservationLimit(userId: Long, scheduleId: Long, ttlSeconds: Long) {
        val key = "ratelimit:user:$userId:schedule:$scheduleId"

        val result = redissonClient.script.eval<Long>(
            RScript.Mode.READ_WRITE,
            loadLua("user_schedule_ratelimit.lua"),
            RScript.ReturnType.INTEGER,
            listOf(key),
            listOf(ttlSeconds.toString())
        )

        if (result == 0L) throw RateLimitException("$ttlSeconds ms에 한 번만 예약할 수 있습니다.")
    }


    private fun loadLua(filename: String): String {
        return javaClass.getResource("/lua/$filename")?.readText()
            ?: throw IllegalArgumentException("Lua 스크립트를 찾을 수 없습니다: $filename")
    }
}