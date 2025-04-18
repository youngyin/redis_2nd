package yin.adapter.out.persistence

import org.springframework.stereotype.Repository
import yin.adapter.out.persistence.entity.UserEntity
import yin.adapter.out.persistence.repository.UserRepository
import yin.application.port.out.UserRepositoryPort
import yin.domain.User

@Repository
class UserAdapter(
    private val userRepository: UserRepository
): UserRepositoryPort {
    /**
     * Finds a user by their ID.
     */
    override fun findById(userId: Long): User? {
        return userRepository.findById(userId)
            .map {
                User(
                    id = it.id,
                    nickname = it.nickname,
                    password = it.password
                )
            }.orElse(null)
    }

    /**
     * Registers a new user.
     */
    override fun save(user: User): User {
        return userRepository.save(UserEntity(
            nickname = user.nickname,
            password = user.password
        )).let {
            User(
                id = it.id,
                nickname = it.nickname,
                password = it.password
            )
        }
    }
}