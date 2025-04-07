package yin.adapter.`in`.aop

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val key: String, // 예: "#seatId"
    val waitTimeSeconds: Long = 3,
)
