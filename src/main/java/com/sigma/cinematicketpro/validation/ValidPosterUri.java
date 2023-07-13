package com.sigma.cinematicketpro.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UriPosterValidator.class)
public @interface ValidPosterUri {
    String message() default "Invalid poster URI";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
