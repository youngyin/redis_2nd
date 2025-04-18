package yin.application.port.out

import yin.domain.User

interface UserRepositoryPort {
    fun findById(userId: Long): User?
    fun save(user: User): User
}