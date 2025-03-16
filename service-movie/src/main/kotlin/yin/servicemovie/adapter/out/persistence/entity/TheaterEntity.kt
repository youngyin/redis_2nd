package yin.servicemovie.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "theaters")
class TheaterEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val name: String,

    @OneToMany(mappedBy = "theater", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val seats: List<SeatEntity> = mutableListOf(),

    @OneToMany(mappedBy = "theater", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val schedules: List<ScheduleEntity> = mutableListOf()
)
