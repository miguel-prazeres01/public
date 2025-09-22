package infrastructure.base.exception;

import application.base.message.message.Response;

public class ErrorResponse extends Response {

    public ErrorResponse(String message, String errorCode) {
        setStatus("ERROR");
        setErrorMessage(message);
        setErrorCode(errorCode);
    }

}
