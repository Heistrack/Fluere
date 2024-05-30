package com.example.fluere.security.config.prodconfig;

import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.KeyPair;


@Configuration
@Profile("prod")
public class AdditionalConfigProd {
    private final KeyPair SECRET_KEYS;

    public AdditionalConfigProd() {
        SECRET_KEYS = Jwts.SIG.RS256.keyPair().build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    public KeyPair jwtKeySupplier() {
        return SECRET_KEYS;
    }
}
