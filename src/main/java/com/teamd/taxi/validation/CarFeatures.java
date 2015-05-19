package com.teamd.taxi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by Anatoliy on 19.05.2015.
 */
@Documented
@Constraint(validatedBy = CarFeaturesValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CarFeatures {

    String message() default "Car features list contains nonexistent features";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
