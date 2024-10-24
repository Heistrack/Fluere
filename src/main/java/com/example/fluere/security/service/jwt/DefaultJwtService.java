package com.example.fluere.security.service.jwt;

import com.example.fluere.security.service.jwtdetails.JwtDetailsService;
import com.example.fluere.userentity.model.AppUser;
import com.example.fluere.userentity.model.UserIdWrapper;
import io.jsonwebtoken.Claims;
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
public class DefaultJwtService implements JwtService {
    private final JwtDetailsService jwtDetailsService;

    @Override
    public String generateToken(Map<String, Object> extraClaims,
                                UserDetails userDetails
    ) {
        return jwtDetailsService.generateToken(extraClaims, userDetails);
    }

    @Override
    public String generateToken(
            UserDetails userDetails
    ) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userId = extractUserId(token);
        return userId.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = jwtDetailsService.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public UserIdWrapper extractUserIdFromRequestAuth(Authentication authentication) {
        AppUser checkedUser = (AppUser) authentication.getPrincipal();
        return checkedUser.getUserId();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
