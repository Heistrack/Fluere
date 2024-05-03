package com.example.final_project.security.service;

import com.example.final_project.userentity.request.appuser.AuthenticationRequest;
import com.example.final_project.userentity.request.appuser.RegisterUserRequest;
import com.example.final_project.security.response.AuthResponseDTO;
import com.example.final_project.security.response.RegisterResponseDTO;
import com.example.final_project.userentity.service.AppUser;
import com.example.final_project.userentity.service.Role;
import com.example.final_project.userentity.service.UserIdWrapper;
import com.example.final_project.userentity.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
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

        return AuthResponseDTO.builder().token(jwtToken).build();
    }
}
