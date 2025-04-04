package yin.application.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import yin.adapter.out.persistence.entity.*
import yin.adapter.out.persistence.repository.*
import yin.application.command.ReserveSeatCommand
import yin.bootstrap.BootstrapApplication
import yin.domain.Genre
import yin.domain.MovieStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Transactional
@SpringBootTest(classes = [BootstrapApplication::class])
class ReserveSeatServiceTest {

 @Autowired
 lateinit var reserveSeatService: ReserveSeatService

 @Autowired
 lateinit var userRepository: UserRepository

 @Autowired
 lateinit var theaterRepository: TheaterRepository

 @Autowired
 lateinit var seatRepository: SeatRepository

 @Autowired
 lateinit var movieRepository: MovieRepository

 @Autowired
 lateinit var scheduleRepository: ScheduleRepository

 @Test
 fun `Given 동일한 좌석과 스케줄 When 여러 사용자가 동시에 예약할 때 Then 하나의 예약만 성공한다`() {
  // Given
  val users = (1..10).map { saveUserEntity() }
  val theater = theaterRepository.save(TheaterEntity(name = "테스트 극장", totalSeats = 100))
  val seat = seatRepository.save(SeatEntity(theater = theater, seatRow = "A", seatNumber = 1))
  val movie = saveMovieEntity()
  val schedule = saveScheduleEntity(movie, theater)

  val commands = users.map { user ->
   ReserveSeatCommand(
    userId = user.id!!,
    scheduleId = schedule.id!!,
    seatId = seat.id!!
   )
  }

  // When
  val successCount = AtomicInteger(0)
  val executor = Executors.newFixedThreadPool(10)
  val tasks = commands.map { command ->
   Callable {
    try {
     reserveSeatService.reserve(command)
     successCount.incrementAndGet()
    } catch (e: Exception) {
     println("실패한 요청: ${e.message}")
    }
   }
  }

  executor.invokeAll(tasks)
  executor.shutdown()

  // Then
  println("성공한 예약 수: ${successCount.get()}")
  assertThat(successCount.get()).isEqualTo(1)
 }


 /**
 * 사용자 정보를 저장합니다.
 */
 private fun saveUserEntity() = userRepository.save(
  UserEntity(nickname = "tester", password = "test")
 )

  /**
  * 영화 정보를 저장합니다.
  */
 private fun saveMovieEntity() = movieRepository.save(
  MovieEntity(
   title = "테스트 영화",
   genre = Genre.ACTION,
   rating = "12세 관람가",
   releaseDate = LocalDate.now(),
   runningTimeMin = 120,
   status = MovieStatus.SHOWING
  )
 )

 /**
  * 극장과 영화 정보를 기반으로 스케줄을 생성합니다.
  */
 private fun saveScheduleEntity(
  movie: MovieEntity,
  theater: TheaterEntity
 ) = scheduleRepository.save(
  ScheduleEntity(
   movie = movie,
   theater = theater,
   startTime = LocalDateTime.now().plusDays(1)
  )
 )
}
