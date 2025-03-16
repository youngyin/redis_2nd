package yin.servicereservation.adapter.out

import jakarta.persistence.*
import yin.servicereservation.domain.ReservationStatus

@Entity
class Reservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val userId: Long,  // 사용자 ID (service-membership에서 관리)
    val scheduleId: Long,  // 상영 일정 ID (service-movie에서 관리)
    val seatId: Long,  // 좌석 ID (service-movie에서 관리)
    @Enumerated(EnumType.STRING)
    val status: ReservationStatus
)
