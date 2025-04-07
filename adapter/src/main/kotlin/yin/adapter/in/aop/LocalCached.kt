package yin.adapter.`in`.aop

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalCached(
    val cacheKeyPrefix: String,
    val ttlSeconds: Long = 300
)