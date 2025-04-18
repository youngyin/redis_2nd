package yin.adapter.out.persistence

import org.springframework.stereotype.Repository
import yin.adapter.out.persistence.repository.SeatRepository
import yin.application.port.out.SeatRepositoryPort
import yin.domain.Seat

@Repository
class SeatAdapter(
    private val seatRepository: SeatRepository
) : SeatRepositoryPort{
    /**
     * Finds a seat by its ID with a lock.
     *
     * @param id The ID of the seat.
     * @return The seat entity.
     */
    override fun findByIdWithLock(id: Long): Seat {
        val findByIdWithLock = seatRepository.findByIdWithLock(id)
        return Seat(
            id = findByIdWithLock.id,
            theaterId = findByIdWithLock.theater.id,
            seatRow = findByIdWithLock.seatRow,
            seatNumber = findByIdWithLock.seatNumber
        )
    }
}