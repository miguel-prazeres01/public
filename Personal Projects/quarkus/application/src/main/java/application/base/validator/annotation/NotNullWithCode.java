package application.base.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import application.base.validator.NotNullWithCodeValidator;

@Constraint(validatedBy = NotNullWithCodeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNullWithCode {
    String message() default "Field cannot be null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int errorCode();
}