package com.hexavolt.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeans {

  @Bean
  public PasswordEncoder passwordEncoder() {
    // cost 12 est un bon compromis aujourd’hui (peut monter à 14 selon tes perfs)
    return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12);
  }
}
