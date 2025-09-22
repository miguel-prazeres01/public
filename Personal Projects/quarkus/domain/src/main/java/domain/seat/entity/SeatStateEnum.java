package domain.seat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeatStateEnum {
    
    EMPTY("Seat is empty"),
    OCCUPIED("Seat is occupied"),
    RESERVING("Seat is being reserved");

    private final String description;
}
