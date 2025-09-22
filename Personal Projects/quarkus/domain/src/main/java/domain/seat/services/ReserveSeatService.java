package domain.seat.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import domain.authentication.jwt.JwtService;
import domain.seat.entity.Seat;
import domain.seat.entity.SeatStateEnum;
import domain.seat.exception.SeatAlreadyOcuppiedException;
import domain.seat.exception.SeatNotFoundException;
import domain.seat.exception.SeatReservedException;
import domain.seat.repository.SeatRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ReserveSeatService {
    private final SeatRepository seatRepository;
    private final JwtService jwtService;

    @Inject
    public ReserveSeatService(SeatRepository seatRepository, JwtService jwtService){
        this.seatRepository = seatRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public Pair<Seat, String> reserveSeat(Long seatId){
        Seat seat = seatRepository.get(seatId).orElseThrow(SeatNotFoundException::new);
        String sessionId = UUID.randomUUID().toString();

        if(seat.getSeatState().equals(SeatStateEnum.OCCUPIED)) throw new SeatAlreadyOcuppiedException();

        if (seat.getSeatState() == SeatStateEnum.RESERVING &&
            seat.getReservationExpiresAt() != null &&
            seat.getReservationExpiresAt().isAfter(LocalDateTime.now())) {
            throw new SeatReservedException();
        }

        seat.setSeatState(SeatStateEnum.RESERVING);
        seat.setReservedBySession(sessionId);
        seat.setReservationExpiresAt(LocalDateTime.now().plusMinutes(4));
        
        seatRepository.save(seat);

        String token = jwtService.generateSessionToken(sessionId);        

        return Pair.of(seat, token);
    }

    @Transactional
    public Pair<List<Seat>, String> reserveSeats(List<Long> seatIds) {
        String sessionId = UUID.randomUUID().toString();
        List<Seat> reservedSeats = new ArrayList<>();

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.get(seatId).orElseThrow(SeatNotFoundException::new);

            if (seat.getSeatState().equals(SeatStateEnum.OCCUPIED)) {
                throw new SeatAlreadyOcuppiedException();
            }

            if (seat.getSeatState() == SeatStateEnum.RESERVING &&
                seat.getReservationExpiresAt() != null &&
                seat.getReservationExpiresAt().isAfter(LocalDateTime.now())) {
                throw new SeatReservedException();
            }

            seat.setSeatState(SeatStateEnum.RESERVING);
            seat.setReservedBySession(sessionId);
            seat.setReservationExpiresAt(LocalDateTime.now().plusMinutes(4));

            reservedSeats.add(seat);
            seatRepository.save(seat);
        }
        String token = jwtService.generateSessionToken(sessionId);

        return Pair.of(reservedSeats, token);
    }
}
