package application.base.exception;


import jakarta.ws.rs.core.Response.Status;
import lombok.Getter;


@Getter
public class ValidationException extends RuntimeException {

    private final Integer errorCode;
    private final Status status;
    private final String messageKey;

    public ValidationException(Integer errorCode, String messageKey, Status status) {
        super(messageKey);
        this.errorCode = errorCode;
        this.status = status;
        this.messageKey = messageKey;

    }    

}
