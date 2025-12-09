package itma.smesharikiback.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class LoginValidator implements ConstraintValidator<Login, String> {
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{4,64}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return LOGIN_PATTERN.matcher(value).matches();
    }
}
