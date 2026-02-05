package com.hexavolt.backend.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.service.JwtService;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepo;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("JWT FILTER HIT: " + request.getMethod() + " " + request.getRequestURI());

        // 🔽 Récupération du token depuis les cookies
        String token = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 🔽 Pas de token → on laisse passer
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 🔽 Extraction de l'email depuis le JWT
            String email = jwtService.getSubject(token);

            if (email != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                userRepo.findByEmail(email).ifPresent(user -> {

                    // 🔽 Authorities MINIMALES (OBLIGATOIRE)
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities);

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    System.out.println("JWT OK FOR: " + email);

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                });
            }

        } catch (Exception ex) {
            // 🔽 JWT invalide → on nettoie le contexte
            SecurityContextHolder.clearContext();
        }

        System.out.println("AUTH IN CONTEXT = " +
                SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);
    }
}
