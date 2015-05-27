package com.teamd.taxi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by Anatoliy on 27.05.2015.
 */
@Documented
@Constraint(validatedBy = GroupProcentBordersValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupProcentBorders {

    String message() default "Discount must be in borders";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
