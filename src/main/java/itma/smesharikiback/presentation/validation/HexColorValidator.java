package itma.smesharikiback.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class HexColorValidator implements ConstraintValidator<HexColor, String> {
    private static final Pattern HEX_COLOR = Pattern.compile("^#[A-Fa-f0-9]{6}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return HEX_COLOR.matcher(value).matches();
    }
}
