package com.teamd.taxi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created on 17-May-15.
 *
 * @author Nazar Dub
 */
public class NotBlankOrNullValidator implements ConstraintValidator<NotBlankOrNull, String> {

    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || s.trim().length() > 0;
    }

    @Override
    public void initialize(NotBlankOrNull constraint) {

    }
}
