package com.sang.demo.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Récupère le header "Authorization"
        String authHeader = request.getHeader("Authorization");

        // 2. Si pas de header ou pas de "Bearer ", on passe au filtre suivant sans rien faire
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extrait le token (tout ce qui vient après "Bearer ")
        String token = authHeader.substring(7);

        // 4. Extrait l'email (subject) du token
        String email = jwtService.extractUsername(token);

        // 5. Si on a un email ET que l'utilisateur n'est pas déjà authentifié dans le contexte
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Charge l'utilisateur depuis la BDD
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 7. Vérifie que le token est valide (bon email + non expiré)
            if (jwtService.isTokenValid(token, userDetails)) {

                // 8. Crée un objet d'authentification Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // 9. Attache les détails de la requête HTTP
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 10. Place l'utilisateur authentifié dans le SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. Continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
