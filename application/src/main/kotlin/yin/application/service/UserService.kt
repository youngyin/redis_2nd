package yin.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.port.`in`.UserUseCase
import yin.application.port.out.UserRepositoryPort
import yin.domain.User

@Service
@Transactional
class UserService(
    private val userRepositoryPort: UserRepositoryPort
) : UserUseCase{
    /**
     * Finds a user by their ID.
     */
    override fun findById(userId: Long): User {
        return userRepositoryPort.findById(userId)
            ?: throw IllegalArgumentException("User not found")
    }

    /**
     * Registers a new user.
     */
    override fun register(user: User): User {
        return userRepositoryPort.save(user)
    }
}