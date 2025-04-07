package yin.adapter.`in`.aop

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cached3Layer(
    val cacheKeyPrefix: String,
    val ttlSeconds: Long = 300,
    val syncThreshold: Int = 5  // Redis로 승격할 최소 hit count
)
