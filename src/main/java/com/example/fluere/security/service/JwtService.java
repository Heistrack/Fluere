package com.example.fluere.security.service;

import com.example.fluere.userentity.model.UserIdWrapper;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    );

    String generateToken(
            UserDetails userDetails
    );

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUserId(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    UserIdWrapper extractUserIdFromRequestAuth(Authentication authentication);
}
