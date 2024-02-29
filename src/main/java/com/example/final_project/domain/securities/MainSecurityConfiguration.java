package com.example.final_project.domain.securities;


import com.example.final_project.domain.securities.exceptions.CustomBasicAuthenticationEntryPoint;
import com.example.final_project.domain.securities.exceptions.SecurityExceptionHandler;
import com.example.final_project.domain.securities.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class MainSecurityConfiguration {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final SecurityExceptionHandler securityExceptionHandler;
    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request
                           .requestMatchers("/api/users/auth").permitAll()
                           .requestMatchers("/api/users/new-user").permitAll()
                           .requestMatchers("/api/x/**").hasAuthority("ROLE_ADMIN")
                           .anyRequest().authenticated()
                   )
                   .csrf(AbstractHttpConfigurer::disable)
                   .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(customBasicAuthenticationEntryPoint))
                   .cors(Customizer.withDefaults())
                   .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                           SessionCreationPolicy.STATELESS))
                   .authenticationProvider(authenticationProvider)
                   .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                   .addFilterBefore(securityExceptionHandler, LogoutFilter.class)
                   .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                   //FIXME add redirection in form login to start page after successful login
                   .build();
    }
}


