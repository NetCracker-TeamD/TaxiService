package com.teamd.taxi.validation;

import com.teamd.taxi.persistence.repository.DriverRepository;
import com.teamd.taxi.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created on 13-May-15.
 *
 * @author Nazar Dub
 */
public class UniqueDriverEmailValidator implements ConstraintValidator<UniqueDriverEmail, String> {

    @Autowired
    private DriverRepository repository;

    @Override
    public void initialize(UniqueDriverEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return repository.findByEmail(email) == null;
    }
}
