package yin.adapter.out

import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import yin.application.port.out.DistributedLockPort
import java.util.concurrent.TimeUnit

@Component
class RedissonLockAdapter(
    private val redissonClient: RedissonClient
) : DistributedLockPort {
    /**
     * Executes a given action with a distributed lock.
     */
    override fun <T> runWithLock(key: String, timeoutSec: Long, action: () -> T): T {
        val lock = redissonClient.getLock(key)
        val locked = lock.tryLock(timeoutSec, 1, TimeUnit.SECONDS)
        if (!locked) throw IllegalStateException("락 획득 실패")
        return try { action() } finally { lock.unlock() }
    }
}