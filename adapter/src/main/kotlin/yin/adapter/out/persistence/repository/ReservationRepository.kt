package yin.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import yin.adapter.out.persistence.entity.ReservationEntity

interface ReservationRepository : JpaRepository<ReservationEntity, Long> {
    fun findAllByUserId(userId: Long): List<ReservationEntity>
}