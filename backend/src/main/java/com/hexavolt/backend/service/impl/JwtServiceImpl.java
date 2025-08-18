package com.hexavolt.backend.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

  @Value("${security.jwt.secret}")
  private String secret;

  @Value("${security.jwt.exp-min:60}")
  private long expMin;

  private Algorithm algo() {
    return Algorithm.HMAC256(secret);
  }

  @Override
  public String generateToken(User u) {
    var now = System.currentTimeMillis();
    return JWT.create()
        .withSubject(u.getEmail())
        .withClaim("uid", u.getId())
        .withClaim("fn", u.getFirstName())
        .withClaim("ln", u.getLastName())
        .withIssuedAt(new Date(now))
        .withExpiresAt(new Date(now + expMin * 60_000))
        .sign(algo());
  }

  @Override
  public String getSubject(String token) {
    return JWT.require(algo()).build().verify(token).getSubject();
  }
}
