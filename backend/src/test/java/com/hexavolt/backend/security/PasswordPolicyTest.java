package com.hexavolt.backend.security;

import org.junit.jupiter.api.Test;

import com.hexavolt.backend.exception.BusinessException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordPolicyTest {

    @Test
    void shouldRejectPasswordContainingEmailLocalPart() {
        assertThrows(BusinessException.class,
            () -> PasswordPolicy.assertNoPersonalInfo("abcUser123!", "User@Example.com", "Jean Dupont"));
    }
}
