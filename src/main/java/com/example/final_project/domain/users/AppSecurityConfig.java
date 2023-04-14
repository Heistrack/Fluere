package com.example.final_project.domain.users;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@Configuration
class AppSecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager customAuthenticationManager(HttpSecurity httpSecurity, UserDetailsService service, PasswordEncoder encoder) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(service).passwordEncoder(encoder).and().build();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        return security
                .cors()
                .disable()
                .csrf()
                .disable()
                .authorizeRequests(
                        req -> req.antMatchers("/users").permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}