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
@Constraint(validatedBy = UniqueUserEmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUserEmail {

    String message() default "User with such email already exist in the system";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
