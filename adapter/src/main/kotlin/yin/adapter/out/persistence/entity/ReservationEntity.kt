package yin.adapter.out.persistence.entity

import jakarta.persistence.*
import yin.domain.ReservationStatus

@Entity
@Table(name = "reservations")
open class ReservationEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    var schedule: ScheduleEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    var seat: SeatEntity,

    @Enumerated(EnumType.STRING)
    var status: ReservationStatus,

    @Version
    var version: Long = 0,
) : BaseTimeEntity()