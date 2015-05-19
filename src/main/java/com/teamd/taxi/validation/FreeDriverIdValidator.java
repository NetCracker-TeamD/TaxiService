package com.teamd.taxi.validation;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.persistence.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Anatoliy on 19.05.2015.
 */
public class FreeDriverIdValidator implements ConstraintValidator<FreeDriverId, Integer> {

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public void initialize(FreeDriverId freeDriverId) {

    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        for (Driver driver : driverRepository.findByCarCarId(null)) {
            if (driver.getId().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
