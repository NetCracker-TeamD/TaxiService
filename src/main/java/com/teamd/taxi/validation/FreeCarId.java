package com.teamd.taxi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created on 14-May-15.
 *
 * @author Nazar Dub
 */
@Documented
@Constraint(validatedBy = FreeCarIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FreeCarId {

    String message() default "Illegal car id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

