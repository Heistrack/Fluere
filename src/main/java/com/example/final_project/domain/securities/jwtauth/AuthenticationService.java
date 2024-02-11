package com.example.final_project.domain.securities.jwtauth;

import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.Role;
import com.example.final_project.domain.users.UserId;
import com.example.final_project.domain.users.WrongCredentialsException;
import com.example.final_project.infrastructure.userRepo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final Supplier<UserId> userIdSupplier;

    public AuthenticationResponse register(RegisterRequest request) {
        //TODO Check this condition
//        if (repository.existsAppUserByEmail(request.email())) {
//            throw new UnableToRegisterException("Such email address is occupied!");
//        }
        AppUser user = AppUser.builder()
                              .id(userIdSupplier.get())
                              .name(request.name())
                              .email(request.email())
                              .password(passwordEncoder.encode(request.password()))
                              .role(Role.USER)
                              .enabled(Boolean.TRUE)
                              .build();

        repository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        //TODO check this statment
        AppUser byEmail = repository.findFirstByEmail(request.email()).get();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        byEmail.id().userId().toString(),
                        request.password()
                )
        );

        AppUser user = repository.findFirstByEmail(request.email())
                                 .orElseThrow(() -> new WrongCredentialsException("Wrong email or password"));
        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
