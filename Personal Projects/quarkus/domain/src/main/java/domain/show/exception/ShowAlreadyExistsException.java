package domain.show.exception;

import domain.base.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class ShowAlreadyExistsException extends BusinessException{
    public ShowAlreadyExistsException(){
        super(1,"error.show.already.exists", Status.FORBIDDEN);
    }

}