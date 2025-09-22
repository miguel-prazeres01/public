package application.base.validator;

import application.base.validator.annotation.NotBlankWithCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class NotBlankWithCodeValidator implements ConstraintValidator<NotBlankWithCode, String> {

    private String message;

    @Override
    public void initialize(NotBlankWithCode constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) { 
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
