package com.teamd.taxi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by Anatoliy on 22.05.2015.
 */
@Documented
@Constraint(validatedBy = ExistingGroupIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingGroupId {

    String message() default "Group with such id is not exist in the system";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
