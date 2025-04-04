package yin.application.port.out

interface RedisSyncPolicy {
    fun shouldSync(key: String, count: Int): Boolean
}