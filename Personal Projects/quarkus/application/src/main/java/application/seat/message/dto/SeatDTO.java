package application.seat.message.dto;

import java.time.LocalDateTime;

import application.base.message.dto.BaseDTO;
import application.seat.message.dto.SeatDTO;
import domain.seat.entity.SeatStateEnum;
import domain.seat.entity.SeatTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SeatDTO extends BaseDTO{

    private String row;
    private Long seatNumber;
    private SeatStateEnum seatState;
    private SeatTypeEnum seatType;
    private LocalDateTime reservationExpiresAt;

    public SeatDTO(Long id, String row, Long seatNumber, SeatStateEnum seatState, SeatTypeEnum seatType, LocalDateTime reservationExpiresAt){
        super(id);
        this.row = row;
        this.seatNumber = seatNumber;
        this.seatState = seatState;
        this.seatType = seatType;
        this.reservationExpiresAt = reservationExpiresAt;
    }
    
}
