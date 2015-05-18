package com.teamd.taxi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created on 13-May-15.
 *
 * @author Nazar Dub
 */
@Documented
@Constraint(validatedBy = DriverFeaturesValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DriverFeatures {

    String message() default "Driver features list contains nonexistent features";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
