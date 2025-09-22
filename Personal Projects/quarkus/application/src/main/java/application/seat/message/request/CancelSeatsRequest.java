package application.seat.message.request;

import java.util.List;

import application.base.validator.annotation.NotNullWithCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CancelSeatsRequest {
    
    @NotNullWithCode(errorCode = 106, message = "error.seat.id.not.null")
    private List<Long> seatIds;
    
}
