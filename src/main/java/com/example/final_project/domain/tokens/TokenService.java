package com.example.final_project.domain.tokens;

import com.example.final_project.api.auth.Token;
import com.example.final_project.domain.users.FluereAppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder encoder;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public Token getToken(Authentication authentication) {
        FluereAppUser appUser = (FluereAppUser) authentication.getPrincipal();
        Instant now = Instant.now();
        long expiry = 1800L;
        Collection<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", authorities)
                .claim("userId", appUser.userId().value())
                .build();

        return Token.newOf(this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
    }


}
