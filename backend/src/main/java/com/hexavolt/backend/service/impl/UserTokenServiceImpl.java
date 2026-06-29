package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;
import com.hexavolt.backend.entity.UserToken.TokenType;
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.repository.UserTokenRepository;
import com.hexavolt.backend.service.TimeService;
import com.hexavolt.backend.service.UserTokenService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
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
        return create(user, TokenType.ACTIVATION, ttl);
    }

    @Override
    public UserToken createResetPasswordToken(User user, Duration ttl) {
        return create(user, TokenType.RESET_PASSWORD, ttl);
    }

    private UserToken create(User user, TokenType type, Duration ttl) {
        if (user == null) {
            throw new BusinessException("Utilisateur obligatoire pour créer un jeton.");
        }

        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new BusinessException("La durée de validité du jeton est invalide.");
        }

        LocalDateTime now = time.now();

        UserToken token = new UserToken();
        token.setUser(user);
        token.setType(type);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusSeconds(ttl.toSeconds()));

        return repo.save(token);
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
        if (user == null) {
            throw new BusinessException("Utilisateur obligatoire.");
        }

        if (cooldown == null || cooldown.isNegative()) {
            throw new BusinessException("Le délai de renvoi est invalide.");
        }

        LocalDateTime after = time.now().minus(cooldown);

        boolean recentExists = repo.existsByUserAndTypeAndCreatedAtAfter(
                user,
                TokenType.ACTIVATION,
                after
        );

        return !recentExists;
    }

    private UserToken validate(String rawToken, TokenType expectedType) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new BusinessException("Jeton obligatoire.");
        }

        UserToken token = repo.findByToken(rawToken)
                .orElseThrow(() -> new BusinessException("Jeton invalide."));

        if (token.getType() != expectedType) {
            throw new BusinessException("Type de jeton invalide.");
        }

        if (token.getUsedAt() != null) {
            throw new BusinessException("Le jeton a déjà été utilisé.");
        }

        if (token.getExpiresAt() == null || time.now().isAfter(token.getExpiresAt())) {
            throw new BusinessException("Le jeton a expiré.");
        }

        return token;
    }

    @Override
    @Transactional
    public void consume(UserToken token) {
        if (token == null) {
            throw new BusinessException("Jeton obligatoire.");
        }

        token.setUsedAt(time.now());
        repo.save(token);
    }

    @Override
    @Transactional
    public void invalidateAllResetTokens(Long userId) {
        if (userId == null) {
            throw new BusinessException("L'identifiant utilisateur est obligatoire.");
        }

        repo.deleteByUser_IdAndType(userId, TokenType.RESET_PASSWORD);
    }
}