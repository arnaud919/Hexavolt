package com.hexavolt.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;
import com.hexavolt.backend.entity.UserToken.TokenType;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    
    Optional<UserToken> findByToken(String token);

    long deleteByUser_IdAndType(Long userId, TokenType type);

    boolean existsByUserAndTypeAndCreatedAtAfter(User user, UserToken.TokenType type, LocalDateTime after);
}
