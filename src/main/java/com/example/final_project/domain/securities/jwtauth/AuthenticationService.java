package com.example.final_project.domain.securities.jwtauth;

import com.example.final_project.api.requests.users.AuthenticationRequest;
import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.authentications.AuthResponseDTO;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.Role;
import com.example.final_project.domain.users.UserIdWrapper;
import com.example.final_project.infrastructure.appuserrepo.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
//TODO remove logs
public class AuthenticationService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Supplier<UserIdWrapper> userIdSupplier;

    public RegisterResponseDTO register(RegisterUserRequest request) {
        AppUser user = AppUser.builder()
                              .userId(userIdSupplier.get())
                              .login(request.login())
                              .email(request.email())
                              .password(passwordEncoder.encode(request.password()))
                              .role(Role.USER)
                              .enabled(Boolean.TRUE)
                              .creationTime(LocalDateTime.now())
                              .build();

        repository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return RegisterResponseDTO.builder().user(user).token(jwtToken).build();
    }

    public AuthResponseDTO authenticate(AuthenticationRequest request) {
        AppUser user = repository.findByLogin(request.login())
                                 .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.userId().id().toString(),
                        request.password()
                )
        );

        String jwtToken = jwtService.generateToken(user);
        log.warn(jwtToken);
        return AuthResponseDTO.builder().token(jwtToken).build();
    }
}
