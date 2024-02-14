package com.example.final_project.domain.securities.jwt;

import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.UserIdWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final int TOKEN_EXPIRATION_TIME_IN_MILI = 1000 * 60 * 60 * 24;
    @Value("${jwt.key}")
    private String SECRET_KEY;

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts.builder()
                   .claims(extraClaims)
                   .subject(userDetails.getUsername())
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME_IN_MILI))
                   .signWith(getSignInKey(), Jwts.SIG.HS256)
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
                   .verifyWith(getSignInKey())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] decodedKeyArray = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(decodedKeyArray);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
