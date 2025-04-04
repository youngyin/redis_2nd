package yin.bootstrap

import lombok.extern.slf4j.Slf4j
import net.datafaker.Faker
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import yin.adapter.out.persistence.entity.*
import yin.adapter.out.persistence.repository.*
import yin.domain.Genre
import yin.domain.MovieStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Slf4j
@Transactional
@Component
class DummyDataInitializerV2(
    private val movieRepository: MovieRepository,
    private val imageRepository: ImageRepository,
    private val theaterRepository: TheaterRepository,
    private val seatRepository: SeatRepository,
    private val scheduleRepository: ScheduleRepository,
    private val userRepository: UserRepository,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        println("DummyDataInitializer: 실행 시작")

        // 0. 기존 데이터 삭제
        scheduleRepository.deleteAll()
        seatRepository.deleteAll()
        imageRepository.deleteAll()
        movieRepository.deleteAll()
        theaterRepository.deleteAll()
        userRepository.deleteAll()
        println("기존 데이터 모두 삭제 완료")

        // 1. 사용자 더미 생성
        (1..10).forEach { i ->
            userRepository.save(
                UserEntity(
                    nickname = "nickname$i",
                    password = "password$i"
                )
            )
        }
        println("사용자 더미 10명 생성 완료")

        // 2. 극장 데이터 생성
        (1..500).forEach {
            theaterRepository.save(
                TheaterEntity(
                    name = "극장 $it",
                    totalSeats = (100..300).random()
                )
            )
        }
        println("극장 더미 생성 완료")

        val theaters = theaterRepository.findAll()

        // 2-1. 좌석 데이터 생성
        theaters.forEach { theater ->
            val rows = listOf("A", "B", "C", "D", "E")
            rows.forEach { row ->
                (1..10).forEach { number ->
                    seatRepository.save(
                        SeatEntity(
                            theater = theater,
                            seatRow = row,
                            seatNumber = number
                        )
                    )
                }
            }
        }
        println("좌석 더미 생성 완료")

        // 3. 랜덤 영화 및 스케줄 생성
        val faker = Faker()
        val genres = Genre.values()

        repeat(500) { i ->
            val movie = MovieEntity(
                title = faker.book().title(),
                genre = genres.random(),
                rating = "12세 관람가",
                releaseDate = LocalDate.now().minusDays((0..1000).random().toLong()),
                runningTimeMin = (80..180).random(),
                status = MovieStatus.SHOWING
            )
            movieRepository.save(movie)

            val image = ImageEntity(movie = movie, url = "https://picsum.photos/200/300?random=$i")
            imageRepository.save(image)

            val scheduleCount = (2..3).random()
            repeat(scheduleCount) {
                val startTime = LocalDateTime.of(
                    LocalDate.now().plusDays((0..30).random().toLong()),
                    LocalTime.of((10..22).random(), listOf(0, 30).random())
                )
                val theater = theaters.random()
                scheduleRepository.save(
                    ScheduleEntity(
                        movie = movie,
                        theater = theater,
                        startTime = startTime
                    )
                )
            }
        }

        println("랜덤 영화 및 스케줄 더미 생성 완료")

        // 4. 테스트용 고정 영화 추가
        val testMovie = MovieEntity(
            title = "테스트 영화",
            genre = Genre.ACTION,
            rating = "15세 관람가",
            releaseDate = LocalDate.of(2023, 12, 1),
            runningTimeMin = 120,
            status = MovieStatus.SHOWING
        )
        movieRepository.save(testMovie)

        val testImage = ImageEntity(
            movie = testMovie,
            url = "https://picsum.photos/200/300?test"
        )
        imageRepository.save(testImage)

        val theater = theaters.first()
        scheduleRepository.save(
            ScheduleEntity(
                movie = testMovie,
                theater = theater,
                startTime = LocalDateTime.now().plusDays(1).withHour(18).withMinute(0)
            )
        )

        println("✔ 테스트 영화 '테스트 영화' 등록 완료")
    }
}
