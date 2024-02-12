package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.AuthResponseDTO;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.infrastructure.userRepo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultUserService {

    private final UserRepository userRepository;
    private final Supplier<UserId> userIdSupplier;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    public AppUser findUserByUsername(String login) {
        return userRepository.findFirstByLogin(login)
                             .orElseThrow();
    }

    public List<UserDetailsResponse> findAll() {
        return userRepository.findAll()
                             .stream()
                             .map(UserDetailsResponse::fromDomain)
                             .collect(Collectors.toList());
    }

    public RegisterResponseDTO registerNewUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UnableToRegisterException("User's email is already occupied!");
        }
        if (userRepository.existsAccountByLogin(request.login())) {
            throw new UnableToRegisterException("User's login is already occupied!");
        }

        return authenticationService.register(request);
    }
//TODO STH NOT RIGHT BELOW
    public UserDetails findByUserNameOrEmail(String usernameOrEmail) {
        return userRepository.findUserDetailsByLoginOrEmail(usernameOrEmail, usernameOrEmail)
                             .orElseThrow(() -> new WrongCredentialsException("Niepoprawne dane logowania"));
    }

    public List<UserDetailsResponse> getAllUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(UserDetailsResponse::fromDomain)
                             .toList();
    }
//TODO Fix it below
    public Optional<UserDetailsResponse> findAppUserByLogin(String login) {
        return Optional.of(userRepository.findFirstByLogin(login)
                                         .map(UserDetailsResponse::fromDomain))
                       .get();
    }

    public Optional<UserDetailsResponse> findAppUserByUserId(String userId) {
        return userRepository.findById(UserId.newId(UUID.fromString(userId)))
                             .map(UserDetailsResponse::fromDomain);
    }
}
