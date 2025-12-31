package com.hexavolt.backend.security;

import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring("Bearer ".length()).trim();

        try {
            String email = jwtService.getSubject(token);

            // Si déjà authentifié, ne rien faire
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // On s'assure que l'utilisateur existe toujours
                var userOpt = userRepo.findByEmail(email);
                if (userOpt.isPresent()) {

                    var auth = new UsernamePasswordAuthenticationToken(
                            email, // principal
                            null,
                            List.of() // roles plus tard
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (Exception ex) {
            // Token invalide / expiré => on laisse Spring renvoyer 401 sur les endpoints protégés
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
