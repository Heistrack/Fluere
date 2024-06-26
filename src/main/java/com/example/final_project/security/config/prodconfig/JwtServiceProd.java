package com.example.final_project.security.config.prodconfig;

import com.example.final_project.security.service.JwtService;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.model.UserIdWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class JwtServiceProd implements JwtService {
    private static final int TOKEN_EXPIRATION_TIME_IN_MILI = 1000 * 60 * 60;
    private final KeyPair keyPair;

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts.builder()
                   .claims(extraClaims)
                   .subject(userDetails.getUsername())
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME_IN_MILI))
                   .signWith(keyPair.getPrivate())
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
        return checkedUser.getUserId();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                   .verifyWith(keyPair.getPublic())
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
