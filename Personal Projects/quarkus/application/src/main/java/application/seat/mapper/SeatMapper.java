package application.seat.mapper;

import application.base.converter.Mapper;
import application.seat.message.dto.SeatDTO;
import domain.seat.entity.Seat;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SeatMapper implements Mapper<Seat, SeatDTO>{

    public SeatDTO toDto(Seat seat){
        return new SeatDTO(seat.getId(), 
             seat.getRow(), seat.getSeatNumber(), seat.getSeatState(), seat.getSeatType(), seat.getReservationExpiresAt());
    }
    
}
