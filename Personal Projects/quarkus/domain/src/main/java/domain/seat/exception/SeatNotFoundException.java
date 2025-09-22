package domain.seat.exception;

import domain.base.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class SeatNotFoundException extends BusinessException{
    public SeatNotFoundException(){
        super(3,"error.seat.not.found", Status.NOT_FOUND);
    }

}
