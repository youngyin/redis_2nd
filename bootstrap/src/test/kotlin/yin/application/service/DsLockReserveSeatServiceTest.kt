package yin.application.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
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
import yin.domain.Reservation
import java.time.LocalDate
import java.time.LocalDateTime

@Transactional
@SpringBootTest(classes = [BootstrapApplication::class])
class DsLockReserveSeatServiceTest {
    @Autowired
    lateinit var dsLockReserveSeatService: DsLockReserveSeatService

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

    @Test
    fun `예약 성공 테스트`() {
        // Given
        val user = saveUserEntity()
        val movie = saveMovieEntity()
        val theater = theaterRepository.save(TheaterEntity(name = "테스트 극장"))
        val schedule = saveScheduleEntity(movie, theater)
        val seat = seatRepository.save(SeatEntity(seatRow = "A", seatNumber = 1, theater = theater))

        val command = ReserveSeatCommand(
            userId = user.id,
            scheduleId = schedule.id,
            seatId = seat.id
        )

        // When
        dsLockReserveSeatService.reserve(command)

        // Then
        val reservations = dsLockReserveSeatService.getAllReservations(user.id)
        assertThat(reservations).hasSize(1)
            .extracting(Reservation::seatId, Reservation::scheduleId, Reservation::userId)
            .contains(Tuple.tuple(seat.id, schedule.id, user.id))

    }
}