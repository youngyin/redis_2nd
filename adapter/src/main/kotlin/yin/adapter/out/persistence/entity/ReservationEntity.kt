package yin.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "reservations")
open class ReservationEntity protected constructor(
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
    var status: String
) : BaseTimeEntity()