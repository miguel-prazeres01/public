package domain.seat.exception;

import domain.base.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class ReservationCancelInvalidException extends BusinessException{
    public ReservationCancelInvalidException(){
        super(15,"error.reservation.invalid.cancel", Status.BAD_REQUEST);
    }

}

