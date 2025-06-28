package com.igorsouza.games.services.jwt;

import java.util.UUID;

public interface JwtService {
    String generateToken(UUID userId);
    String validateToken(String token);
}
