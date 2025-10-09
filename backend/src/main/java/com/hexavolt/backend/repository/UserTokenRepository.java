package com.hexavolt.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.UserToken;
import com.hexavolt.backend.entity.UserToken.TokenType;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByToken(String token);

    long deleteByUser_IdAndType(Integer userId, TokenType type);
}
