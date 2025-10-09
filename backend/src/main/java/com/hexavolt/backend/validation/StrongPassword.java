package com.hexavolt.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Mot de passe trop faible";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
