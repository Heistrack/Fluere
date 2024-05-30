package com.example.fluere.security.config.devconfig;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

@Configuration
@Profile("dev")
public class AdditionalConfigDev {
    private final SecretKey SECRET_KEY;

    public AdditionalConfigDev() {
        String hardcodedKey = "KdDH0U3gfwvXjr9fhgNlzuWS//E6VfBx+ljNl/BMZ4I=";
        byte[] constantKeyBytes = hardcodedKey.getBytes();
        SecretKey constantKey = Keys.hmacShaKeyFor(constantKeyBytes);
        this.SECRET_KEY = constantKey;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public SecretKey jwtKeySupplier() {
        return this.SECRET_KEY;
    }
}
