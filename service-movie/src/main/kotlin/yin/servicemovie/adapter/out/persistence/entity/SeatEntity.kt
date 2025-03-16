package yin.servicemovie.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "seats")
class SeatEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    val theater: TheaterEntity,

    @Column(nullable = false)
    val seatRow: String, // A, B, C, D, E

    @Column(nullable = false)
    val seatNumber: Int // 1, 2, 3, 4, 5
)
