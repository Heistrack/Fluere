package com.example.fluere.security.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.security.KeyPair;

@Configuration
public class AdditionalSecurityConfig {
    private final SecretKey SECRET_KEY;
    private final KeyPair SECRET_KEYS;

    public AdditionalSecurityConfig() {
        String hardcodedKey = "KdDH0U3gfwvXjr9fhgNlzuWS//E6VfBx+ljNl/BMZ4I=";
        byte[] constantKeyBytes = hardcodedKey.getBytes();
        SecretKey constantKey = Keys.hmacShaKeyFor(constantKeyBytes);
        this.SECRET_KEY = constantKey;
        SECRET_KEYS = Jwts.SIG.RS256.keyPair().build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.property.service.security-setup", havingValue = "demo")
    PasswordEncoder demoPasswordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    @ConditionalOnProperty(name = "app.property.service.security-setup", havingValue = "demo")
    public SecretKey demoJwtKeySupplier() {
        return this.SECRET_KEY;
    }

    @Bean
    @ConditionalOnProperty(name = "app.property.service.security-setup", havingValue = "full")
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    @ConditionalOnProperty(name = "app.property.service.security-setup", havingValue = "full")
    public KeyPair jwtKeySupplier() {
        return SECRET_KEYS;
    }
}
