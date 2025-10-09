package com.hexavolt.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    // Liste courte pour bloquer les évidences
    private static final Set<String> VERY_COMMON = Set.of(
        "password","azerty","qwerty","123456","123456789","000000","111111","letmein","admin"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return false;

        // Pas d'espaces
        if (value.chars().anyMatch(Character::isWhitespace)) return false;

        // Longueur >= 12
        if (value.length() < 12) return false;

        // Au moins 1 minuscule, 1 majuscule, 1 chiffre, 1 spécial
        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;
        for (char c : value.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c))    hasDigit = true;
            else                               hasSpecial = true;
        }
        if (!(hasLower && hasUpper && hasDigit && hasSpecial)) return false;

        // Interdire quelques mots de passe trop communs (insensible à la casse)
        if (VERY_COMMON.contains(value.toLowerCase())) return false;

        return true;
    }
}

