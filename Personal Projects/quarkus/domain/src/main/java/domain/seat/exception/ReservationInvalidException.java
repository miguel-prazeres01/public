package domain.seat.exception;

import domain.base.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class ReservationInvalidException extends BusinessException{
    public ReservationInvalidException(){
        super(6,"error.reservation.invalid.occupied", Status.BAD_REQUEST);
    }

}
