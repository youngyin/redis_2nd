package yin.application.port.out

interface DistributedLockPort {
    fun <T> runWithLock(key: String, waitSec: Long = 3, timeoutSec: Long = 10, action: () -> T): T
}