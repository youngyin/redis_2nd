package yin.adapter.out

import org.springframework.stereotype.Component
import yin.application.port.out.RedisSyncPolicy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Component
class ThresholdBasedRedisSyncPolicy(
) : RedisSyncPolicy {
    private val accessCounter = ConcurrentHashMap<String, AtomicInteger>()
    private val threshold = 5 // 동기화 기준 카운트

    /**
     * Redis 동기화 정책
     */
    override fun shouldSync(key: String, count: Int): Boolean {
        val count: Int = accessCounter.computeIfAbsent(key) { AtomicInteger(0) }.incrementAndGet()
        return count >= threshold
    }
}
