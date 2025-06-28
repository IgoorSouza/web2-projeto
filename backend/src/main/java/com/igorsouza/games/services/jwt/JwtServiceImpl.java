package com.igorsouza.games.services.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_DURATION}")
    private String jwtDuration;

    @Value("${JWT_ISSUER}")
    private String jwtIssuer;

    @Override
    public String generateToken(UUID userId) {
        try {
            Instant expiresAt = Instant.now().plusMillis(Long.parseLong(jwtDuration));

            return JWT.create()
                    .withIssuer(jwtIssuer)
                    .withSubject(String.valueOf(userId))
                    .withExpiresAt(expiresAt)
                    .sign(Algorithm.HMAC256(jwtSecret));
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar token JWT.");
        }
    }

    @Override
    public String validateToken(String token) {
        return JWT.require(Algorithm.HMAC256(jwtSecret))
                .withIssuer(jwtIssuer)
                .build()
                .verify(token)
                .getSubject();
    }
}
