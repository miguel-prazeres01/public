package application.base.validator;

import application.base.validator.annotation.StringSizeWithCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StringSizeWithCodeValidator implements ConstraintValidator<StringSizeWithCode, String> {
    
    private int min;
    private int max;
    private String message;

    @Override
    public void initialize(StringSizeWithCode annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
        this.message = annotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values should be handled by @NotNull if required
        }
        
        int length = value.length();
        if (length < min || length > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                message)
                .addConstraintViolation();
            return false;
        }
        return true;
    }
}