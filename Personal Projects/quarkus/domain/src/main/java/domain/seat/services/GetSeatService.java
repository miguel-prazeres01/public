package domain.seat.services;

import java.util.List;

import domain.seat.entity.Seat;
import domain.seat.exception.SeatNotFoundException;
import domain.seat.repository.SeatRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetSeatService {
    private final SeatRepository seatRepository;

    @Inject
    public GetSeatService(SeatRepository seatRepository){
        this.seatRepository = seatRepository;
    }

    public Seat getSeatById(Long seatId){
        return seatRepository.get(seatId).orElseThrow(SeatNotFoundException::new);
    }

    public List<Seat> getAllSeats(Long showId){
        return seatRepository.findAllSeatsByShow(showId);
    }
}
