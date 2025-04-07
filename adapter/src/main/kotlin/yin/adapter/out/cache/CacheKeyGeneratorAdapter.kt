package yin.adapter.out.cache

import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import yin.application.port.out.CacheKeyGeneratorPort

@Component
class CacheKeyGeneratorAdapter : CacheKeyGeneratorPort {

    /**
     * 캐시 키 생성
     * @param joinPoint ProceedingJoinPoint
     * @param prefix 캐시 키 prefix
     * @return 캐시 키
     *
     * joinPoint.args를 이용하여 캐시 키 생성
     * - String, Number, Boolean 타입은 그대로 사용
     * - 나머지 타입은 hashCode()로 처리
     */
    override fun generate(joinPoint: ProceedingJoinPoint, prefix: String): String {
        val args = joinPoint.args
            .filterNotNull()
            .joinToString(":") {
                when (it) {
                    is String, is Number, is Boolean -> it.toString()
                    else -> it.hashCode().toString() // 복잡 객체는 해시로 처리
                }
            }

        return "$prefix:$args"
    }
}