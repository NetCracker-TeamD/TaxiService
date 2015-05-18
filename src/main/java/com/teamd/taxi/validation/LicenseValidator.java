package com.teamd.taxi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created on 13-May-15.
 *
 * @author Nazar Dub
 */
public class LicenseValidator implements ConstraintValidator<License, String> {

    private static final Pattern PATTERN_LICENSE = Pattern.compile("^[A-Z]\\d{6}$");

    @Override
    public void initialize(License constraintAnnotation) {
    }

    @Override
    public boolean isValid(String license, ConstraintValidatorContext context) {
        return license == null || PATTERN_LICENSE.matcher(license).matches();
    }
}
