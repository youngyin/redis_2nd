package yin.adapter.out.ratelimit

import org.redisson.client.codec.StringCodec
import org.redisson.api.RScript
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import yin.application.port.out.RateLimitPort
import yin.infrastructure.error.RateLimitException
import java.nio.charset.StandardCharsets

@Service
class RedisRateLimitAdapter(
    private val redissonClient: RedissonClient
) : RateLimitPort {

    override fun checkIpRequestLimit(ip: String?) {
        val normalizedIp = (ip ?: "unknown").replace(":", "_")
        val key = "ratelimit:ip:$normalizedIp:minute"
        val blockedKey = "ratelimit:ip:$normalizedIp:blocked"
        val now = System.currentTimeMillis()
        val limit = 20
        val expireMillis = 60_000L

        val luaScript = loadLua("ip_ratelimit.lua")
        val result = redissonClient
            .getScript(StringCodec.INSTANCE)
            .eval<Long>(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                listOf(key, blockedKey),
                limit.toString(),
                now.toString(),
                expireMillis.toString()
            )

        println("result: $result")
        if (result == null || result == 0L) {
            throw RateLimitException("요청이 너무 많습니다. 1분 후 다시 시도해주세요.")
        }
    }

    override fun checkReservationLimit(userId: Long, scheduleId: Long, ttlMillis: Long) {
        val key = "ratelimit:user:$userId:schedule:$scheduleId"
        val limit = ttlMillis
        val now = System.currentTimeMillis()

        println("[RedisRateLimitAdapter] key: $key, limit: $limit, now: $now, ttlMillis: $ttlMillis")

        val luaScript = loadLua("user_schedule_ratelimit.lua")
        val result = redissonClient
            .getScript(StringCodec.INSTANCE)
            .eval<Long>(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                listOf(key),
                limit.toString(),
                now.toString(),
                ttlMillis.toString()
            )

        if (result == 0L) {
            throw RateLimitException("${ttlMillis / 1000}초에 한 번만 예약할 수 있습니다.")
        }
    }

    private fun loadLua(filename: String): String {
        val path = "/lua/$filename"
        val url = javaClass.getResource(path)
        println("[Lua] 경로: $path → $url")

        val text = url?.readText(StandardCharsets.UTF_8)
        println("[Lua] 파일 내용 유무: ${text != null}")
        return text ?: throw IllegalArgumentException("Lua 스크립트를 찾을 수 없습니다: $filename")
    }

}