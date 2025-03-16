package yin.servicemovie.domain

import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDate

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
data class Movie(
    val id: Long,
    val title: String,
    val genre: Genre,
    val releaseDate: LocalDate,
    val runningTime: Int,
    val status: MovieStatus
)
