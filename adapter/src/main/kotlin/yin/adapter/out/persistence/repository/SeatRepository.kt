package yin.adapter.out.persistence.repository

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import yin.adapter.out.persistence.entity.SeatEntity

interface SeatRepository : JpaRepository<SeatEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatEntity s WHERE s.id = :id")
    fun findByIdWithLock(@Param("id") id: Long): SeatEntity

}