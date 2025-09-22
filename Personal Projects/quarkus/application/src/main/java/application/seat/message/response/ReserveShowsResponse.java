package application.seat.message.response;

import java.util.List;

import application.seat.message.dto.SeatDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ReserveShowsResponse {
    
    private List<SeatDTO> seats;
    private String token;
}
