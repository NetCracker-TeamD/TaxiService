package com.teamd.taxi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by Anatoliy on 19.05.2015.
 */
@Documented
@Constraint(validatedBy = CarModelNameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CarModelName {

    String message() default "Name's model of car contains invalid characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String regexpError() default "";
}
