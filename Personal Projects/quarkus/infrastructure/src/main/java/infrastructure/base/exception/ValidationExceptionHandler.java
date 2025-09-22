package infrastructure.base.exception;



import application.base.exception.ValidationException;

import infrastructure.base.configuration.MessagePropertyReader;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ValidationException> {

    @Inject
    MessagePropertyReader messagePropertyReader;

    @Override
    public Response toResponse(ValidationException exception) {
        String errorMessage = messagePropertyReader.getMessage(exception.getMessageKey());
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, exception.getErrorCode().toString());
        return Response.status(exception.getStatus()).entity(errorResponse).build();
    }

}