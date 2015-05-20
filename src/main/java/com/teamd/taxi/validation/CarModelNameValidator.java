package com.teamd.taxi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created by Anatoliy on 19.05.2015.
 */
public class CarModelNameValidator implements ConstraintValidator<CarModelName, String> {

    private Pattern pattern;

    @Override
    public void initialize(CarModelName carModelName) {
        this.pattern = Pattern.compile(carModelName.regexpError());
    }

    @Override
    public boolean isValid(String modelName, ConstraintValidatorContext constraintValidatorContext) {
        if(modelName==null || modelName.replaceAll(" ","").length()==0) return true;
        if(this.pattern.matcher(modelName).find())
            return false;
        return true;
    }
}
