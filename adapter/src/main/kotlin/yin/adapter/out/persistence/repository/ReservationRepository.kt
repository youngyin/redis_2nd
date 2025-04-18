package yin.adapter.out.persistence.repository

import io.lettuce.core.dynamic.annotation.Param
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import yin.adapter.out.persistence.entity.ReservationEntity

interface ReservationRepository : JpaRepository<ReservationEntity, Long> {
    fun findAllByUserId(userId: Long): List<ReservationEntity>
    fun existsByScheduleIdAndSeatId(scheduleId: Long, seatId: Long): Boolean

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT r FROM ReservationEntity r WHERE r.schedule.id = :scheduleId AND r.seat.id = :seatId")
//    fun findWithLockByScheduleIdAndSeatId(
//        @Param("scheduleId") scheduleId: Long,
//        @Param("seatId") seatId: Long
//    ): ReservationEntity?
}