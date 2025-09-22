package domain.seat.exception;

import domain.base.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class SeatAlreadyOcuppiedException extends BusinessException{
    public SeatAlreadyOcuppiedException(){
        super(4,"error.seat.already.occupied", Status.BAD_REQUEST);
    }

}

