package pl.sgorski.AirLink.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        final int minLength = 8;
        final int maxLength = 50;
        final Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&^\\-+=_><,.\\[\\](){}|\\\\/:;\"'~]+$");

        return value != null &&
                pattern.matcher(value).matches() &&
                value.length() >= minLength &&
                value.length() <= maxLength;
    }
}
