package com.teamd.taxi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Anatoliy on 27.05.2015.
 */
public class GroupProcentBordersValidator implements ConstraintValidator<GroupProcentBorders, String> {

    @Override
    public void initialize(GroupProcentBorders groupProcentBorders) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s==null || s.trim().length() == 0) return true;

        Float discount = Float.parseFloat(s);
        if(discount>=0 && discount<=100){
            return true;
        }
        return false;
    }
}
