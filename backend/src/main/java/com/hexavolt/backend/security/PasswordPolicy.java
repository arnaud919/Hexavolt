package com.hexavolt.backend.security;

public final class PasswordPolicy {
    private PasswordPolicy() {}

    public static void assertNoPersonalInfo(String password, String email, String name) {
        if (password == null) throw new IllegalArgumentException("Un mot de passe est requis.");
        String p = password.toLowerCase();

        if (email != null) {
            String local = email.toLowerCase().split("@")[0];
            if (!local.isBlank() && p.contains(local)) {
                throw new IllegalArgumentException("Le mot de passe ne doit pas contenir votre adresse e-mail.");
            }
            String domain = email.toLowerCase().substring(email.indexOf('@') + 1);
            String domainLabel = domain.contains(".") ? domain.substring(0, domain.indexOf('.')) : domain;
            if (!domainLabel.isBlank() && p.contains(domainLabel)) {
                throw new IllegalArgumentException("Le mot de passe ne doit pas contenir le domaine de votre adresse e-mail.");
            }
        }
        if (name != null) {
            String n = name.toLowerCase().replaceAll("\\s+", "");
            if (!n.isBlank() && p.contains(n)) {
                throw new IllegalArgumentException("Le mot de passe ne doit pas contenir votre nom.");
            }
        }
    }
}
