package com.teamd.taxi.validation;

import org.hibernate.annotations.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on 17-May-15.
 *
 * @author Nazar Dub
 */
@Documented
@Constraint(validatedBy = NotBlankOrNullValidator.class)
@java.lang.annotation.Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankOrNull {
    String message() default "{org.hibernate.validator.constraints.NotBlankOrNull.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
