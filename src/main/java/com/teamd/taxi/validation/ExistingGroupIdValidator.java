package com.teamd.taxi.validation;

import com.teamd.taxi.persistence.repository.GroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Anatoliy on 22.05.2015.
 */
public class ExistingGroupIdValidator implements ConstraintValidator<ExistingGroupId, Integer> {

    @Autowired
    private GroupsRepository repository;

    @Override
    public void initialize(ExistingGroupId existingGroupId) {

    }

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext constraintValidatorContext) {
        return id == null || repository.findOne(id) != null;
    }
}
