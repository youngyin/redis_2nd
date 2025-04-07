package yin.application.service

import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import yin.adapter.out.persistence.entity.*
import yin.adapter.out.persistence.repository.*
import yin.application.command.ReserveSeatCommand
import yin.application.service.lock.DsLockReserveSeatService
import yin.bootstrap.BootstrapApplication
import yin.domain.Genre
import yin.domain.MovieStatus
import yin.infrastructure.util.FunctionalTransactionExecutor
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test

//@Transactional
@SpringBootTest(classes = [BootstrapApplication::class])
class CountDownLatchTest {
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

    @Autowired
    lateinit var transactionExecutor: FunctionalTransactionExecutor

    @Test
    fun `동시에 100명이 예약 시도할 때 하나만 성공`() {
        val users = (1..100).map { saveUserEntity() }
        val theater = theaterRepository.save(TheaterEntity(name = "테스트 극장", totalSeats = 100))
        val seat = seatRepository.save(SeatEntity(theater = theater, seatRow = "A", seatNumber = 1))
        val movie = saveMovieEntity()
        val schedule = saveScheduleEntity(movie, theater)

        val commands = users.map {
            ReserveSeatCommand(it.id!!, schedule.id!!, seat.id!!)
        }

        val latch = java.util.concurrent.CountDownLatch(1)
        val successCount = AtomicInteger(0)
        val executor = Executors.newFixedThreadPool(20)

        val futures = commands.map { command ->
            executor.submit {
                latch.await() // 대기하다가
                try {
                    transactionExecutor.execute {
                        dsLockReserveSeatService.reserve(command)
                        successCount.incrementAndGet()
                    }
                } catch (e: Exception) {
                    println("실패한 요청: ${e.message}")
                }
            }
        }

        latch.countDown() // 모든 스레드 출발!

        futures.forEach { it.get() }
        executor.shutdown()

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