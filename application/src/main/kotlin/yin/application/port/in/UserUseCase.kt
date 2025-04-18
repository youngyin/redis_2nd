package yin.application.port.`in`

import yin.domain.User

interface UserUseCase {
    fun findById(userId: Long): User
    fun register(user: User): User
}