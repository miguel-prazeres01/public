package domain.offers.testService.exception;

import domain.base.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class TestServiceException extends BusinessException{
    public TestServiceException(){
        super(1, "error.test", Status.NOT_FOUND);
    }
}