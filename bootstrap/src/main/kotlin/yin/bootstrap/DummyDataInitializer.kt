package yin.bootstrap

import net.datafaker.Faker
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import yin.adapter.out.persistence.entity.ImageEntity
import yin.adapter.out.persistence.entity.MovieEntity
import yin.adapter.out.persistence.repository.ImageRepository
import yin.adapter.out.persistence.repository.MovieRepository
import yin.domain.Genre
import yin.domain.MovieStatus
import java.time.LocalDate

@Component
class DummyDataInitializer(
    private val movieRepository: MovieRepository,
    private val imageRepository: ImageRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        if (movieRepository.count() < 500) {
            val faker = Faker()
            val genres = Genre.values()
            repeat(500) {
                val movie = MovieEntity(
                    title = faker.book().title(),
                    genre = genres.random(),
                    rating = "12세 관람가",
                    releaseDate = LocalDate.now().minusDays((0..1000).random().toLong()),
                    runningTimeMin = (80..180).random(),
                    status = MovieStatus.SHOWING
                )
                movieRepository.save(movie)

                // 썸네일 이미지 더미도 같이
                val image = ImageEntity(movie = movie, url = "https://picsum.photos/200/300?random=$it")
                imageRepository.save(image)
            }
        }
    }
}
