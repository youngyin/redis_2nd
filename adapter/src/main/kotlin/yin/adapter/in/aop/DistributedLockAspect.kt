package yin.adapter.`in`.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import yin.application.port.out.DistributedLockPort

@Aspect
@Component
class DistributedLockAspect(
    private val lockPort: DistributedLockPort
) {
    private val logger = LoggerFactory.getLogger(DistributedLockAspect::class.java)
    private val parser: ExpressionParser = SpelExpressionParser()

    /**
     * 분산 락 처리
     */
    @Around("@annotation(distributedLock)")
    fun proceedWithLock(joinPoint: ProceedingJoinPoint, distributedLock: DistributedLock): Any {
        val key = resolveLockKey(joinPoint, distributedLock.key)
        val waitTime = distributedLock.waitTimeSeconds

        logger.info("🔒 [LOCK] Try acquire: $key (wait=$waitTime)")

        return lockPort.runWithLock(
            key = key,
            waitSec = distributedLock.waitTimeSeconds,
            timeoutSec = distributedLock.timeoutSeconds,
        ) {
            try {
                joinPoint.proceed()
            } catch (e: Throwable) {
                logger.error("🔒 [LOCK] 실행 중 예외 발생 - key=$key", e)
                throw e
            } finally {
                logger.info("🔒 [UNLOCK] Release: $key")
            }
        }
    }

    private fun resolveLockKey(joinPoint: ProceedingJoinPoint, keySpEL: String): String {
        val method = (joinPoint.signature as MethodSignature).method
        val paramNames = method.parameters.map { it.name }
        val paramValues = joinPoint.args

        val context = StandardEvaluationContext().apply {
            paramNames.forEachIndexed { index, name ->
                setVariable(name, paramValues[index])
            }
        }

        return parser.parseExpression(keySpEL).getValue(context)?.toString()
            ?: throw IllegalArgumentException("분산 락 키 생성 실패: $keySpEL")
    }
}
