package application.base.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import application.base.validator.NotBlankWithCodeValidator;

@Constraint(validatedBy = NotBlankWithCodeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotBlankWithCode {
    String message() default "Field cannot be blank";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int errorCode();
}