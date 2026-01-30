// src/main/java/com/hexavolt/backend/service/impl/UserTokenServiceImpl.java
package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;
import com.hexavolt.backend.entity.UserToken.TokenType;
import com.hexavolt.backend.repository.UserTokenRepository;
import com.hexavolt.backend.service.TimeService;
import com.hexavolt.backend.service.UserTokenService;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserTokenServiceImpl implements UserTokenService {

    private final UserTokenRepository repo;
    private final TimeService time;

    public UserTokenServiceImpl(UserTokenRepository repo, TimeService time) {
        this.repo = repo;
        this.time = time;
    }

    @Override
    public UserToken createActivationToken(User user, Duration ttl) {
        return create(user, UserToken.TokenType.ACTIVATION, ttl);
    }

    @Override
    public UserToken createResetPasswordToken(User user, Duration ttl) {
        return create(user, UserToken.TokenType.RESET_PASSWORD, ttl);
    }

    private UserToken create(User user, UserToken.TokenType type, Duration ttl) {
        var t = new UserToken();
        t.setUser(user);
        t.setType(type);
        t.setToken(UUID.randomUUID().toString());
        t.setCreatedAt(time.now());
        t.setExpiresAt(time.now().plusSeconds(ttl.toSeconds()));
        return repo.save(t);
    }

    @Override
    @Transactional(readOnly = true)
    public UserToken validateActivationToken(String rawToken) {
        return validate(rawToken, TokenType.ACTIVATION);
    }

    @Override
    @Transactional(readOnly = true)
    public UserToken validateResetPasswordToken(String rawToken) {
        return validate(rawToken, TokenType.RESET_PASSWORD);
    }

    @Override
    public boolean canResendActivation(User user, Duration cooldown) {
        LocalDateTime after = LocalDateTime.now().minus(cooldown);
        boolean recentExists = repo.existsByUserAndTypeAndCreatedAtAfter(
                user, UserToken.TokenType.ACTIVATION, after);
        return !recentExists;
    }

    private UserToken validate(String rawToken, TokenType expectedType) {
        UserToken tk = repo.findByToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("Jeton invalide."));
        if (tk.getType() != expectedType) {
            throw new IllegalArgumentException("Type de jeton invalide.");
        }
        if (tk.getUsedAt() != null) {
            throw new IllegalArgumentException("Le jeton a déjà été utilisé.");
        }
        if (tk.getExpiresAt() == null || LocalDateTime.now().isAfter(tk.getExpiresAt())) {
            throw new IllegalArgumentException("Le jeton a expiré.");
        }
        return tk;
    }

    @Override
    @Transactional
    public void consume(UserToken token) {
        token.setUsedAt(LocalDateTime.now());
        repo.save(token);
    }

    @Override
    @Transactional
    public void invalidateAllResetTokens(Long userId) {
        repo.deleteByUser_IdAndType(userId, TokenType.RESET_PASSWORD);
    }
}
