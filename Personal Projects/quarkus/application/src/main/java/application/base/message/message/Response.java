package application.base.message.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class Response {

    private String status = "SUCCESS";
    private String errorCode = null;
    private String errorMessage = null;    

}
