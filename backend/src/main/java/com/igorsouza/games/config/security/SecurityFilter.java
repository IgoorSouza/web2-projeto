package com.igorsouza.games.config.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.igorsouza.games.models.User;
import com.igorsouza.games.services.jwt.JwtService;
import com.igorsouza.games.services.users.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    private static final String[] AUTH_WHITELIST = {
            "/auth/register",
            "/auth/login",
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        for (String path : AUTH_WHITELIST) {
            if (request.getRequestURI().equals(path)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String token = recoverToken(request);

        if (token == null) {
            sendErrorResponse(response, "Autenticação necessária.");
            return;
        }

        try {
            UUID userId = UUID.fromString(jwtService.validateToken(token));
            User user = userService.getUserById(userId);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            sendErrorResponse(response, "Token de autenticação inválido ou expirado.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o token de autenticação.", e);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorMessage);
        response.getWriter().flush();
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }
}
