package com.teamd.taxi.validation;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.persistence.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created on 14-May-15.
 *
 * @author Nazar Dub
 */
public class FreeCarIdValidator implements ConstraintValidator<FreeCarId, Integer> {

    @Autowired
    private CarRepository repository;

    @Override
    public void initialize(FreeCarId constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        for (Car c : repository.findByDriverId(null)) {
            if (c.getCarId().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
