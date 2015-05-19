package com.teamd.taxi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by Anatoliy on 19.05.2015.
 */
@Documented
@Constraint(validatedBy = FreeDriverIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FreeDriverId {

    String message() default "Illegal driver id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
