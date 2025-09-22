package application.seat.message.request;

import application.base.validator.annotation.NotNullWithCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReserveShowRequest {
    
    @NotNullWithCode(errorCode = 106, message = "error.seat.id.not.null")
    private Long seatId;
}
