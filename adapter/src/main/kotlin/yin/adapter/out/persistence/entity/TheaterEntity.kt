package yin.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "theaters")
open class TheaterEntity public constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: String,
    @Column(name = "total_seats")
    var totalSeats: Int = 0,
) :BaseTimeEntity()