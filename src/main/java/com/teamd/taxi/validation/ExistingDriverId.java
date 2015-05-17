package com.teamd.taxi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created on 17-May-15.
 *
 * @author Nazar Dub
 */
@Documented
@Constraint(validatedBy = ExistingDriverIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingDriverId {

    String message() default "Driver with such id is not exist in the system";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
