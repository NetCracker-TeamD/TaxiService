package com.teamd.taxi.validation;

import com.teamd.taxi.persistence.repository.DriverRepository;
import com.teamd.taxi.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created on 20-May-15.
 *
 * @author Oleg Chorniy
 */
public class UniqueUserEmailValidator implements ConstraintValidator<UniqueUserEmail, String> {

    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(UniqueUserEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return true; //для предотвращения исключений при поиске в БД
        }
        return repository.findByEmail(email) == null;
    }
}
