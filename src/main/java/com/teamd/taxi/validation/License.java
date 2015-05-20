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
@Constraint(validatedBy = LicenseValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface License {

    String message() default "License number has illegal format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
