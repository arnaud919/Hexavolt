package com.hexavolt.backend.security;

import com.hexavolt.backend.exception.BusinessException;

public final class PasswordPolicy {

    private PasswordPolicy() {
    }

    public static void assertNoPersonalInfo(String password, String email, String name) {
        if (password == null || password.isBlank()) {
            throw new BusinessException("Un mot de passe est requis.");
        }

        String normalizedPassword = password.toLowerCase();

        checkEmailNotIncluded(normalizedPassword, email);
        checkNameNotIncluded(normalizedPassword, name);
    }

    private static void checkEmailNotIncluded(String normalizedPassword, String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        String normalizedEmail = email.toLowerCase();

        String localPart = getEmailLocalPart(normalizedEmail);
        if (!localPart.isBlank() && normalizedPassword.contains(localPart)) {
            throw new BusinessException("Le mot de passe ne doit pas contenir votre adresse e-mail.");
        }

        String domainLabel = getEmailDomainLabel(normalizedEmail);
        if (!domainLabel.isBlank() && normalizedPassword.contains(domainLabel)) {
            throw new BusinessException("Le mot de passe ne doit pas contenir le domaine de votre adresse e-mail.");
        }
    }

    private static void checkNameNotIncluded(String normalizedPassword, String name) {
        if (name == null || name.isBlank()) {
            return;
        }

        String normalizedName = name.toLowerCase().replaceAll("\\s+", "");

        if (!normalizedName.isBlank() && normalizedPassword.contains(normalizedName)) {
            throw new BusinessException("Le mot de passe ne doit pas contenir votre nom.");
        }
    }

    private static String getEmailLocalPart(String email) {
        int atIndex = email.indexOf('@');

        if (atIndex <= 0) {
            return "";
        }

        return email.substring(0, atIndex);
    }

    private static String getEmailDomainLabel(String email) {
        int atIndex = email.indexOf('@');

        if (atIndex < 0 || atIndex + 1 >= email.length()) {
            return "";
        }

        String domain = email.substring(atIndex + 1);
        int dotIndex = domain.indexOf('.');

        if (dotIndex <= 0) {
            return domain;
        }

        return domain.substring(0, dotIndex);
    }
}