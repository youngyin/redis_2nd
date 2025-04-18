package yin.application.port.out

import yin.domain.Seat

interface SeatRepositoryPort {
    fun findByIdWithLock(id: Long): Seat
}