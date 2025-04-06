package yin.adapter.out

import lombok.extern.slf4j.Slf4j
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import yin.application.port.out.DistributedLockPort
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

@Slf4j
@Component
class RedissonLockAdapter(
    private val redissonClient: RedissonClient
) : DistributedLockPort {
    private val log = LoggerFactory.getLogger(this::class.java)
    /**
     * Executes a given action with a distributed lock.
     */
    override fun <T> runWithLock(key: String, timeoutSec: Long, action: () -> T): T {
        val logId = UUID.randomUUID().toString()
        val lock = redissonClient.getLock(key)
        log.info("[$logId] 락 시도 - key: $key, timeout: ${timeoutSec}s")
        val locked = lock.tryLock(timeoutSec, 5, TimeUnit.SECONDS) // leaseTime 증가

        if (!locked) {
            log.error("[$logId] 락 획득 실패 - key: $key, timeout: ${timeoutSec}s")
            throw IllegalStateException("락 획득 실패")
        }
        return try {
            log.info("[$logId] 락 획득 성공 - key: $key, timeout: ${timeoutSec}s")
            action()
        } finally {
            log.info("[$logId] 락 해제 - key: $key, timeout: ${timeoutSec}s")
            lock.unlock()
        }
    }
}