package application.base.validator;

import application.base.validator.annotation.NotNullWithCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotNullWithCodeValidator implements ConstraintValidator<NotNullWithCode, Object> {

    private String message;

    @Override
    public void initialize(NotNullWithCode constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
