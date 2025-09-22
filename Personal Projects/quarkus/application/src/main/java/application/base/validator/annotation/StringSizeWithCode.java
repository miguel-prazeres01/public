package application.base.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import application.base.validator.StringSizeWithCodeValidator;

@Constraint(validatedBy = StringSizeWithCodeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StringSizeWithCode {
    String message() default "String size is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int errorCode();
    int min() default 0;
    int max() default Integer.MAX_VALUE;
}