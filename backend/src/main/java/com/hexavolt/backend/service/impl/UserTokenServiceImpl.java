// src/main/java/com/hexavolt/backend/service/impl/UserTokenServiceImpl.java
package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;
import com.hexavolt.backend.repository.UserTokenRepository;
import com.hexavolt.backend.service.TimeService;
import com.hexavolt.backend.service.UserTokenService;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    public UserToken createResetToken(User user, Duration ttl) {
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
    public UserToken consumeActivationToken(String token) {
        return consume(token, UserToken.TokenType.ACTIVATION);
    }

    @Override
    public UserToken consumeResetToken(String token) {
        return consume(token, UserToken.TokenType.RESET_PASSWORD);
    }

    private UserToken consume(String token, UserToken.TokenType expected) {
        var t = repo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (t.getType() != expected) {
            throw new IllegalArgumentException("Invalid token type");
        }
        if (t.getUsedAt() != null) {
            throw new IllegalArgumentException("Token already used");
        }
        if (t.getExpiresAt().isBefore(time.now())) {
            throw new IllegalArgumentException("Token expired");
        }
        t.setUsedAt(time.now());
        return repo.save(t);
    }
}
