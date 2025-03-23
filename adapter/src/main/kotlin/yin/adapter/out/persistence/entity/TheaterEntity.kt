package yin.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "theaters")
open class TheaterEntity protected constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: String,
    @Column(name = "total_seats")
    var totalSeats: Int
) :BaseTimeEntity()