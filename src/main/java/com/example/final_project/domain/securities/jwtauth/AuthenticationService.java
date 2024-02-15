package com.example.final_project.domain.securities.jwtauth;

import com.example.final_project.api.requests.users.AuthenticationRequest;
import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.authentications.AuthResponseDTO;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.Role;
import com.example.final_project.domain.users.UserIdWrapper;
import com.example.final_project.domain.users.exceptions.WrongCredentialsException;
import com.example.final_project.infrastructure.userRepo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
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
                              .build();

        repository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return RegisterResponseDTO.builder().user(user).token(jwtToken).build();
    }

    public AuthResponseDTO authenticate(AuthenticationRequest request) {
        AppUser user = repository.findByLogin(request.login())
                                 .orElseThrow(() -> new WrongCredentialsException("Wrong login or password."));

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
