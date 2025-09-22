package domain.base.exception;

import jakarta.ws.rs.core.Response.Status;

public class UserNotRegisteredException extends BusinessException{
    public UserNotRegisteredException(){
        super(10042,"error.user.not.registered.exception", Status.FORBIDDEN);
    }

}