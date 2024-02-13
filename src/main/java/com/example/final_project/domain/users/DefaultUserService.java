package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.infrastructure.userRepo.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.key.value}")
    private String ADMIN_PASSWORD;

    public RegisterResponseDTO registerNewUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UnableToRegisterException("User's email is already occupied!");
        }
        if (userRepository.existsByLogin(request.login())) {
            throw new UnableToRegisterException("User's login is already occupied!");
        }

        return authenticationService.register(request);
    }

    public AppUser findFromToken(String userId) {
        return userRepository.findById(UserId.newId(UUID.fromString(userId)))
                             .orElseThrow(() -> new JwtException("Invalid token"));
    }

    public List<UserDetailsResponse> getAllUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(UserDetailsResponse::fromDomain).toList();
    }

    public UserDetailsResponse findByUserId(String userId) {
        return userRepository.findById(UserId.newId(UUID.fromString(userId)))
                             .map(UserDetailsResponse::fromDomain)
                             .orElseThrow(() -> new NoSuchElementException("There is no such user id!"));
    }

    public AppUser findByLogin(String login) {
        return userRepository.findByLogin(login)
                             .orElseThrow(() -> new NoSuchElementException("There is no user with such login"));
    }

    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new NoSuchElementException("There is no user with such email"));
    }

    public void removeUserByLogin(String login) {
        Optional<UserId> userId = userRepository.findByLogin(login).map(AppUser::id);
        userId.ifPresent(userRepository::deleteById);

        registerAdminUser();
    }

    public void removeUserByUserId(String userId) {
        Optional<UserId> userToRemove = userRepository.findById(UserId.newId(UUID.fromString(userId)))
                                                      .map(AppUser::id);
        userToRemove.ifPresent(userRepository::deleteById);

        registerAdminUser();
    }

    public void removeThemAll() {
        userRepository.deleteAll();
        registerAdminUser();
    }

    @PostConstruct
    private void registerAdminUser() {
        if (!userRepository.existsByLogin("admin")) {
            AppUser admin = AppUser.builder()
                                   .id(UserId.newId(UUID.randomUUID()))
                                   .login("admin")
                                   .email("X")
                                   .password(passwordEncoder.encode(ADMIN_PASSWORD))
                                   .role(Role.ADMIN)
                                   .enabled(true)
                                   .build();
            userRepository.save(admin);
        }
    }
}
