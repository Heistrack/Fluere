package com.example.final_project.domain.securities;


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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class MainSecurityConfiguration {

    private static final String ADMIN_PASSWORD = "12345";
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final PasswordEncoder passwordEncoder;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request
                                                  .requestMatchers("/budgets/**").authenticated()
                                                  .requestMatchers("/expenses/**").authenticated()
                                                  .requestMatchers("/users/**").permitAll()
                                                  .requestMatchers("/**").permitAll()
                                                  .anyRequest().authenticated()
//                           .requestMatchers("/api/**").hasRole("USER")
                   )
                   .csrf(AbstractHttpConfigurer::disable)
                   //TODO Check how to implement csrf token properly
//                   .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                                     .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                   .httpBasic(Customizer.withDefaults())
                   .cors(Customizer.withDefaults())
                   .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                           SessionCreationPolicy.STATELESS))
                   .authenticationProvider(authenticationProvider)
                   .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                   .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                   //TODO add exception handling for security filter chain
                   /* .exceptionHandling((exceptions) -> exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));*/
                   //FIXME add redirection in form login to start page after successful login
                   // add exception handling for /*.exceptionHandling((exceptions) -> exceptions
                   //                           .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                   //                           .accessDeniedHandler(new BearerTokenAccessDeniedHandler())*/
                   .build();
    }
}


