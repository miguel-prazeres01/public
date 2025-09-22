package domain.seat.services;

import java.util.ArrayList;
import java.util.List;

import domain.seat.entity.Seat;
import domain.seat.entity.SeatStateEnum;
import domain.seat.exception.ReservationCancelInvalidException;
import domain.seat.exception.SeatNotFoundException;
import domain.seat.repository.SeatRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CancelSeatService {

    private final SeatRepository seatRepository;

    @Inject
    public CancelSeatService(SeatRepository seatRepository){
        this.seatRepository = seatRepository;
    }

    @Transactional
    public Seat cancelSeat(Long seatId, String sessionToken){
        Seat seat = seatRepository.get(seatId)
            .orElseThrow(SeatNotFoundException::new);

        if (seat.getSeatState() != SeatStateEnum.RESERVING
            || !sessionToken.equals(seat.getReservedBySession())) {
            throw new ReservationCancelInvalidException();
        }

        seat.setSeatState(SeatStateEnum.EMPTY);

        seat.setReservedBySession(null);
        seat.setReservationExpiresAt(null);

        seatRepository.save(seat);

        return seat;
    }

    @Transactional
    public List<Seat> cancelSeats(List<Long> seatIds, String sessionToken) {
        List<Seat> cancelled = new ArrayList<>();

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.get(seatId)
                .orElseThrow(SeatNotFoundException::new);

            if (seat.getSeatState() != SeatStateEnum.RESERVING
                || !sessionToken.equals(seat.getReservedBySession())) {
                throw new ReservationCancelInvalidException();
            }

            seat.setSeatState(SeatStateEnum.EMPTY);
            seat.setReservedBySession(null);
            seat.setReservationExpiresAt(null);
            seat.setTransactionId(null);

            seatRepository.save(seat);
            cancelled.add(seat);
        }

        return cancelled;
    }

    
}
