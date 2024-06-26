package com.example.final_project.security.service;

import com.example.final_project.security.request.AuthenticationRequest;
import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.security.response.AuthResponseDTO;
import com.example.final_project.security.response.RegisterResponseDTO;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.model.Role;
import com.example.final_project.userentity.model.UserIdWrapper;
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
        AppUser user = AppUser.newOf(
                userIdSupplier.get(),
                request.login(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.USER,
                Boolean.TRUE,
                LocalDateTime.now()
        );

        repository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return RegisterResponseDTO.newOf(user, jwtToken);
    }

    public AuthResponseDTO authenticate(AuthenticationRequest request) {
        AppUser user = repository.findByLogin(request.login())
                                 .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUserId().id().toString(),
                        request.password()
                )
        );

        String jwtToken = jwtService.generateToken(user);

        return AuthResponseDTO.newOf(user.getUserId().id(), jwtToken);
    }
}
