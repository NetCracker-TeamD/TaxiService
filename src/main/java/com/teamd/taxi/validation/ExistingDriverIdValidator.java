package com.teamd.taxi.validation;

import com.teamd.taxi.persistence.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created on 17-May-15.
 *
 * @author Nazar Dub
 */
public class ExistingDriverIdValidator implements ConstraintValidator<ExistingDriverId , Integer> {

    @Autowired
    private DriverRepository repository;

    @Override
    public void initialize(ExistingDriverId constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext context) {
        return id == null || repository.findOne(id) != null;
    }
}
