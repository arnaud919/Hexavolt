package com.hexavolt.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    /**
     * Bean du filtre JWT.
     * 
     * @ConditionalOnMissingBean permet de le mocker dans les tests.
     */
    @Bean
    @ConditionalOnMissingBean
    JwtAuthFilter jwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        return new JwtAuthFilter(jwtService, userRepository);
    }

    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
            JwtAuthFilter jwtAuthFilter) throws Exception {

        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // 🔓 Auth publique
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/verify/resend").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/verify").permitAll()
                        .requestMatchers("/api/auth/password/**").permitAll()
                        .requestMatchers("/api/public/stations/*/photo").permitAll()
                        .requestMatchers("/api/public/stations/*/video").permitAll()

                        // 🔐 Auth protégée
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

                        // 🌍 Public métier
                        .requestMatchers(HttpMethod.GET, "/api/cities/**").permitAll()

                        // 🏢 Métier protégé
                        .requestMatchers("/api/profile/**").authenticated()
                        .requestMatchers("/api/locations/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/stations").authenticated()

                        .anyRequest().authenticated())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                (request, response, authException) -> response
                                        .setStatus(HttpServletResponse.SC_UNAUTHORIZED)))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
