package com.example.final_project.domain.securities.jwt;

import com.example.final_project.domain.users.appusers.AppUser;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final int TOKEN_EXPIRATION_TIME_IN_MILI = 1000 * 60 * 60 * 24;
    private final KeySupplier keySupplier;

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts.builder()
                   .claims(extraClaims)
                   .subject(userDetails.getUsername())
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME_IN_MILI))
                   .signWith(keySupplier.getKeys().getPrivate())
                   .header()
                   .add("typ", "JWT")
                   .and()
                   .compact();
    }

    public String generateToken(
            UserDetails userDetails
    ) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userId = extractUserId(token);
        return userId.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public UserIdWrapper extractUserIdFromRequestAuth(Authentication authentication) {
        AppUser checkedUser = (AppUser) authentication.getPrincipal();
        return checkedUser.userId();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                   .verifyWith(keySupplier.getKeys().getPublic())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
