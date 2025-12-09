package itma.smesharikiback.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = LoginValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {
    String message() default "Логин может содержать буквы, цифры и символы ._- длиной от 4 до 64.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
