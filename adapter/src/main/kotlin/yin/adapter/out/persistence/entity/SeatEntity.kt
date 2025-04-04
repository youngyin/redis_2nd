package yin.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "seats")
open class SeatEntity public constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    var theater: TheaterEntity,
    @Column(name = "seat_row")
    var seatRow: String,
    @Column(name = "seat_number")
    var seatNumber: Int
) :BaseTimeEntity()