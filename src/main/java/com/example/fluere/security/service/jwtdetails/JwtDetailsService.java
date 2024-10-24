package com.example.fluere.security.service.jwtdetails;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtDetailsService {
    String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    );

    Claims extractAllClaims(String token);
}
