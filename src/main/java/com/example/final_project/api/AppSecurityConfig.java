package com.example.final_project.api;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@EnableWebSecurity
public class AppSecurityConfig {
    @Bean
    public InMemoryUserDetailsManager manager() {

        var user = User.withUsername("krzychu_123")
                .password("{noop}password")
                .roles("ADMIN")
                .build();

        var admin = User.withUsername("user2")
                .password("{noop}password")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        return security
                .cors(AbstractHttpConfigurer::disable)
                .csrf()
                .disable()
                .authorizeRequests(
                        req -> req.antMatchers("/**").hasRole("ADMIN")
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}