package com.example.final_project.domain.securities;

import com.example.final_project.domain.users.appusers.UserIdWrapper;
import com.example.final_project.infrastructure.appuserrepo.AppUserRepository;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.crypto.SecretKey;

@Configuration
public class AdditionalConfiguration {
    private final AppUserRepository repository;
    //TODO remove constant key on release
//    private final KeyPair SECRET_KEYS;
    private final SecretKey SECRET_KEY;

    public AdditionalConfiguration(AppUserRepository repository) {
        this.repository = repository;
//        this.SECRET_KEYS = Jwts.SIG.RS256.keyPair().build();
        String hardcodedKey = "KdDH0U3gfwvXjr9fhgNlzuWS//E6VfBx+ljNl/BMZ4I=";
        byte[] constantKeyBytes = hardcodedKey.getBytes();

        SecretKey constantKey = Keys.hmacShaKeyFor(constantKeyBytes);
        this.SECRET_KEY = constantKey;
    }
//TODO add this below

    //    @Bean
//    public KeySupplier jwtKeySupplier() {
//        return () -> SECRET_KEYS;
//    }
//TODO remove key below
    @Bean
    public SecretKey jwtKeySupplier() {
        SecretKey key = this.SECRET_KEY;
        return key;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return id -> repository.findById(UserIdWrapper.newFromString(id))
                               .orElseThrow(() -> new BadCredentialsException("Wrong user or password."));
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        //TODO change it to 13 value after dev
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                         .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                         .findAndAddModules()
                         .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }
}
