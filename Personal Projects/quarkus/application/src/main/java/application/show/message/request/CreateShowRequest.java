package application.show.message.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import application.base.validator.annotation.NotBlankWithCode;
import application.base.validator.annotation.NotNullWithCode;
import application.base.validator.annotation.StringSizeWithCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateShowRequest {
    
    @NotBlankWithCode(errorCode = 100, message = "error.name.not.blank")
    @StringSizeWithCode(errorCode = 102, max = 100, message = "error.name.max.length")
    private String name;

    @NotNullWithCode(errorCode = 101, message = "error.message.date.time.not.null")
    private LocalDateTime dateTime;
    
    @NotNullWithCode(errorCode = 103, message = "error.price.not.null")
    private BigDecimal price;
    
    @NotNullWithCode(errorCode = 104, message = "error.row.number.not.null")
    private Long rowsNumber;
    
    @NotNullWithCode(errorCode = 105, message = "error.column.number.not.null")
    private Long columnNumber;
}
