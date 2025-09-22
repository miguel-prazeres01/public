package infrastructure.base.exception;

import domain.base.exception.BusinessException;
import infrastructure.base.configuration.MessagePropertyReader;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionHandler implements ExceptionMapper<BusinessException> {

    @Inject
    MessagePropertyReader messagePropertyReader;

    @Override
    public Response toResponse(BusinessException exception) {
        String errorMessage = messagePropertyReader.getMessage(exception.getMessageKey());
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, exception.getErrorCode().toString());
        return Response.status(exception.getStatus()).entity(errorResponse).build();
    }

}
