package application.seat.message.request;

import application.base.validator.annotation.NotBlankWithCode;
import application.base.validator.annotation.NotNullWithCode;
import application.base.validator.annotation.StringSizeWithCode;
import domain.phoneValidator.PhoneNumber;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InitPaymentShowRequest {
    
    @NotNullWithCode(errorCode = 107, message = "error.seat.id.not.null")
    private Long seatId;

    @NotBlankWithCode(errorCode = 108, message = "error.buyer.name.not.blank")
    @StringSizeWithCode(errorCode = 109, max = 100, message = "error.buyer.name.max.length")
    private String buyerName;

    @Email
    @NotBlankWithCode(errorCode = 110, message = "error.email.not.blank")
    @StringSizeWithCode(errorCode = 111, max = 100, message = "error.email.max.length")
    private String buyerEmail;

    @PhoneNumber
    @NotBlankWithCode(errorCode = 112, message = "error.phone.number.not.blank")
    private String buyerPhone;
}
