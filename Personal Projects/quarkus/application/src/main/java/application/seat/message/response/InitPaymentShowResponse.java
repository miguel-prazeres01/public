package application.seat.message.response;

import application.seat.message.dto.SeatDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class InitPaymentShowResponse {
    private SeatDTO seatDTO;
}
