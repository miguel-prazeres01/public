package application.seat.message.response;

import application.seat.message.dto.SeatDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ReserveShowResponse {
    
    private SeatDTO seat;
    private String token;
}
