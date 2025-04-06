package yin.infrastructure.util

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class FunctionalTransactionExecutor {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun <T> execute(action: () -> T): T {
        return action()
    }
}