package com.hexavolt.backend.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordPolicyTest {

    @Test
    void shouldRejectPasswordContainingEmailLocalPart() {
        assertThrows(IllegalArgumentException.class,
            () -> PasswordPolicy.assertNoPersonalInfo("abcUser123!", "User@Example.com", "Jean Dupont"));
    }

    @Test
    void shouldRejectWeakPassword() {
        assertThrows(IllegalArgumentException.class,
            () -> PasswordPolicy.assertStrongEnough("short1!"));
    }

    @Test
    void shouldRejectMissingSymbol() {
        assertThrows(IllegalArgumentException.class,
            () -> PasswordPolicy.assertStrongEnough("MotDePasse2025")); // pas de symbole
    }
}
