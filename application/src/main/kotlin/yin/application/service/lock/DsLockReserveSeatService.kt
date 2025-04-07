package yin.application.service.lock

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yin.application.command.ReserveSeatCommand
import yin.application.port.`in`.Lock.DsLockReservationUseCase
import yin.application.port.out.DistributedLockPort
import yin.application.port.out.ReserveSeatPort
import yin.domain.Reservation
import yin.domain.ReservationStatus

@Transactional(readOnly = true)
@Service
class DsLockReserveSeatService(
    private val reserveSeatPort: ReserveSeatPort,
    private val seatLockManager: DistributedLockPort,
) : DsLockReservationUseCase {

    /**
     * Reserves a seat for a given user and schedule.
     *
     * @param command The reservation command containing user ID, schedule ID, and seat ID.
     * @return The reserved seat information.
     */
    @Transactional
    override fun reserve(command: ReserveSeatCommand): Reservation {
        return seatLockManager.runWithLock("seat:${command.seatId}") {
            if (reserveSeatPort.existsByScheduleIdAndSeatId(command.scheduleId, command.seatId)) {
                throw IllegalStateException("이미 예약된 좌석입니다.")
            }

            val reservation = Reservation(
                userId = command.userId,
                scheduleId = command.scheduleId,
                seatId = command.seatId,
                status = ReservationStatus.PENDING
            )

            reserveSeatPort.reserveSeat(reservation)
        }
    }
}