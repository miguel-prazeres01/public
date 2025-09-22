package infrastructure.seat.repository;

import java.time.LocalDateTime;
import java.util.List;

import domain.seat.entity.Seat;
import domain.seat.entity.SeatStateEnum;
import domain.seat.repository.SeatRepository;
import infrastructure.base.repository.BaseRepositoryImpl;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SeatRepositoryImpl extends BaseRepositoryImpl<Seat> implements SeatRepository{
    @Override
    public List<Seat> findAllSeatsByShow(Long showId){
        return find("show.id = ?1", showId).list();
    }

    @Override
    public List<Seat> findByTransactionId(String transactionId){
        return find ("transactionId = ?1", transactionId).list();
    }

    @Override
    public List<Seat> findAllBySeatStateAndReservationExpiresAtBefore(SeatStateEnum seatStateEnum, LocalDateTime dateTime){
        return find("seatState = ?1 and reservationExpiresAt < ?2", seatStateEnum, dateTime).list();
    }

    @Override
    public List<Seat> findAllBySeatsPending(SeatStateEnum seatStateEnum, LocalDateTime dateTime){
        return find("seatState = ?1 and reservationExpiresAt > ?2", seatStateEnum, dateTime).list();
    }
}
