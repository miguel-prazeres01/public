package domain.seat.repository;

import java.time.LocalDateTime;
import java.util.List;

import domain.base.repository.BaseRepository;
import domain.seat.entity.Seat;
import domain.seat.entity.SeatStateEnum;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface SeatRepository extends BaseRepository<Seat>{
    List<Seat> findAllBySeatStateAndReservationExpiresAtBefore(
        SeatStateEnum state, LocalDateTime before);

    List<Seat> findAllBySeatsPending(
        SeatStateEnum state, LocalDateTime after);

    List<Seat> findAllSeatsByShow(Long showId);

    List<Seat> findByTransactionId(String transactionId);
}
