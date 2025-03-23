package yin.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "schedules")
open class ScheduleEntity protected constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    var movie: MovieEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    var theater: TheaterEntity,
    @Column(name = "start_time")
    var startTime: LocalDateTime
) : BaseTimeEntity()