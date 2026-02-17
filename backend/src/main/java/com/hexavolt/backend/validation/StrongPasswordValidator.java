package com.hexavolt.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private static final Set<String> VERY_COMMON = Set.of(
        "password","azerty","qwerty","123456","123456789","000000","111111","letmein","admin"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return false;

        if (value.chars().anyMatch(Character::isWhitespace)) return false;

        if (value.length() < 12) return false;

        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;
        for (char c : value.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c))    hasDigit = true;
            else                               hasSpecial = true;
        }
        if (!(hasLower && hasUpper && hasDigit && hasSpecial)) return false;

        if (VERY_COMMON.contains(value.toLowerCase())) return false;

        return true;
    }
}

