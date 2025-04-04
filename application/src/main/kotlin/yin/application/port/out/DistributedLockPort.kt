package yin.application.port.out

interface DistributedLockPort {
    fun <T> runWithLock(key: String, timeoutSec: Long = 3, action: () -> T): T
}