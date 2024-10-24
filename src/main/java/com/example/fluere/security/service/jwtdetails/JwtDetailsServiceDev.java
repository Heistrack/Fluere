package com.example.fluere.security.service.jwtdetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.property.service.security-setup", havingValue = "demo")
public class JwtDetailsServiceDev implements JwtDetailsService {
    private static final int TOKEN_EXPIRATION_TIME_IN_MILI = 1000 * 60 * 60;
    private final SecretKey hardcodedKey;

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts.builder()
                   .claims(extraClaims)
                   .subject(userDetails.getUsername())
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME_IN_MILI))
                   .signWith(hardcodedKey)
                   .header()
                   .add("typ", "JWT")
                   .and()
                   .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                   .verifyWith(hardcodedKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }
}


