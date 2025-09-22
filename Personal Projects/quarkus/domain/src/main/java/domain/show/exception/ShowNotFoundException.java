package domain.show.exception;

import domain.base.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class ShowNotFoundException extends BusinessException{
    public ShowNotFoundException(){
        super(2,"error.show.not.found", Status.FORBIDDEN);
    }

}
