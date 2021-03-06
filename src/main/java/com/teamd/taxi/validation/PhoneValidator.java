package com.teamd.taxi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final Pattern PATTERN = Pattern.compile("^\\+?[ ()0-9\\-]+$");

    @Override
    public void initialize(Phone customConstraints) {
    }

    // If phoneNumber equals null return false; Changed by Nazar Dub on 20.05.15
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        return phoneNumber == null || PATTERN.matcher(phoneNumber).matches();
    }
}