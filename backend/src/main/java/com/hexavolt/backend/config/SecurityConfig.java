package com.hexavolt.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.security.JwtAuthFilter;
import com.hexavolt.backend.service.JwtService;

@Configuration
public class SecurityConfig {

  @Bean
  JwtAuthFilter jwtAuthFilter(JwtService jwtService, UserRepository userRepo) {
    return new JwtAuthFilter(jwtService, userRepo);
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // API stateless
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/verify/resend").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/auth/verify").permitAll()
            .requestMatchers("/api/auth/password/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/cities/search").permitAll()
            .requestMatchers("/api/auth/me").authenticated()
            .anyRequest().authenticated() // à durcir plus tard
        )
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
