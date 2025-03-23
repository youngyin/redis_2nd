package yin.adapter.out.persistence.entity

import jakarta.persistence.*
import yin.domain.Genre
import yin.domain.MovieStatus
import java.time.LocalDate

@Entity
@Table(name = "movies")
open class MovieEntity public constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var title: String,
    @Enumerated(EnumType.STRING)
    var genre: Genre,
    var rating: String,
    @Column(name = "release_date")
    var releaseDate: LocalDate,
    @Column(name = "running_time")
    var runningTimeMin: Int,
    @Enumerated(EnumType.STRING)
    var status: MovieStatus
) : BaseTimeEntity()
