package com.teamd.taxi.validation;

import com.teamd.taxi.persistence.repository.CarRepository;
import com.teamd.taxi.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Anatoliy on 19.05.2015.
 */
public class ExistingCarIdValidator implements ConstraintValidator<ExistingCarId,Integer> {

    @Autowired
    private CarRepository carRepository;

    @Override
    public void initialize(ExistingCarId existingCarId) {

    }

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext constraintValidatorContext) {
        return id == null || carRepository.findOne(id) != null;
    }
}
