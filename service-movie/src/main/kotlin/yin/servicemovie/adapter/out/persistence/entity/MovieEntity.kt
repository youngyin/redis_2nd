package yin.servicemovie.adapter.out.persistence.entity

import jakarta.persistence.*
import yin.servicemovie.domain.Genre
import yin.servicemovie.domain.Movie
import yin.servicemovie.domain.MovieStatus
import java.time.LocalDate

@Entity
@Table(name = "movies")
class MovieEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val title: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val genre: Genre,

    val releaseDate: LocalDate,
    val runningTime: Int, // 분 단위

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: MovieStatus,

    @OneToMany(mappedBy = "movie", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val schedules: List<ScheduleEntity> = mutableListOf(),

    @OneToMany(mappedBy = "movie", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val images: List<ImageEntity> = mutableListOf()
) {
    fun toDomain(): Movie = Movie(
        id = id,
        title = title,
        genre = genre,
        releaseDate = releaseDate,
        runningTime = runningTime,
        status = status
    )

    companion object {
        fun fromDomain(movie: Movie) = MovieEntity(
            id = movie.id,
            title = movie.title,
            genre = movie.genre,
            releaseDate = movie.releaseDate,
            runningTime = movie.runningTime,
            status = movie.status
        )
    }
}
