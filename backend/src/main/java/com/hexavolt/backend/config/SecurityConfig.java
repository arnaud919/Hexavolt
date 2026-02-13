package com.hexavolt.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.security.JwtAuthFilter;
import com.hexavolt.backend.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

  @Bean
  JwtAuthFilter jwtAuthFilter(JwtService jwtService, UserRepository userRepo) {
    return new JwtAuthFilter(jwtService, userRepo);
  }

  @Bean
  @Order(1)
  SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

    http
        .securityMatcher("/api/**") // 🔥 CLÉ ABSOLUE
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth

            // Auth publique
            .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/verify/resend").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/auth/verify").permitAll()
            .requestMatchers("/api/auth/password/**").permitAll()

            // Auth protégée
            .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

            // Public
            .requestMatchers(HttpMethod.GET, "/api/cities/**").permitAll()

            // Métier
            .requestMatchers("/api/profile/**").authenticated()
            .requestMatchers("/api/locations/**").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/stations").authenticated()

            .anyRequest().authenticated())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(
                (request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
