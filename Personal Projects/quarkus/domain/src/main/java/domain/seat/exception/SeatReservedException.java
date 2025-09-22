package domain.seat.exception;

import domain.base.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class SeatReservedException extends BusinessException{
    public SeatReservedException(){
        super(5,"error.seat.reserved", Status.BAD_REQUEST);
    }

}
