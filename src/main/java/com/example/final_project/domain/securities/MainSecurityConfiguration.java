package com.example.final_project.domain.securities;


import com.example.final_project.domain.securities.exceptions.CustomAccessDeniedHandlerJwt;
import com.example.final_project.domain.securities.exceptions.CustomAuthenticationEntryPointJwt;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class MainSecurityConfiguration {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAuthenticationEntryPointJwt customAuthenticationEntryPointJwt;
    private final CustomAccessDeniedHandlerJwt customAccessDeniedHandlerJwt;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request
                           //TODO configure endpoints security properly
                           .requestMatchers("/budgets/**").authenticated()
                           .requestMatchers("/expenses/**").authenticated()
                           .requestMatchers("/**").permitAll()
                           .anyRequest().authenticated()
                   )
                   .csrf(AbstractHttpConfigurer::disable)
                   .httpBasic(Customizer.withDefaults())
                   .cors(Customizer.withDefaults())
                   .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                           SessionCreationPolicy.STATELESS))
                   .authenticationProvider(authenticationProvider)
                   .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                   .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                   .exceptionHandling(exceptionHandlers -> exceptionHandlers.authenticationEntryPoint(
                                                                                    customAuthenticationEntryPointJwt)
                                                                            .accessDeniedHandler(
                                                                                    customAccessDeniedHandlerJwt))
                   //FIXME add redirection in form login to start page after successful login
                   .build();
    }
}


