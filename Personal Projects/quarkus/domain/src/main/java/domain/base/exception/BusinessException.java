package domain.base.exception;

import lombok.Getter;
import jakarta.ws.rs.core.Response.Status;


@Getter
public class BusinessException extends RuntimeException {

    private final Integer errorCode;
    private final Status status;
    private final String messageKey;

    public BusinessException(Integer errorCode, String messageKey, Status status) {
        super(messageKey);
        this.errorCode = errorCode;
        this.status = status;
        this.messageKey = messageKey;

    }    

}
