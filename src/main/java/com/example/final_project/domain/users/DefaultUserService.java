package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.AuthenticationRequest;
import com.example.final_project.api.requests.users.EmailChangeRequest;
import com.example.final_project.api.requests.users.PasswordChangeRequest;
import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetService;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.domain.users.exceptions.UnableToCreateException;
import com.example.final_project.infrastructure.userRepo.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    private final BudgetService budgetService;

    @Value("${admin.key.value}")
    private String ADMIN_PASSWORD;

    @Override
    public RegisterResponseDTO registerNewUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UnableToCreateException("User's email is already occupied!");
        }
        if (userRepository.existsByLogin(request.login())) {
            throw new UnableToCreateException("User's login is already occupied!");
        }

        return authenticationService.register(request);
    }

    @Override
    public AppUser findFromToken(String userId) {
        return userRepository.findById(UserIdWrapper.newFromString(userId))
                             .orElseThrow(() -> new JwtException("Invalid token"));
    }

    @Override
    public List<UserDetailsResponse> getAllUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(UserDetailsResponse::fromDomain).toList();
    }

    @Override
    public UserDetailsResponse findByUserId(UUID userId) {
        return userRepository.findById(UserIdWrapper.newOf(userId))
                             .map(UserDetailsResponse::fromDomain)
                             .orElseThrow(() -> new NoSuchElementException("There is no such user id!"));
    }

    @Override
    public AppUser findByLogin(String login) {
        return userRepository.findByLogin(login)
                             .orElseThrow(() -> new NoSuchElementException("There is no user with such login"));
    }

    @Override
    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new NoSuchElementException("There is no user with such email"));
    }

    @Override
    public void removeUserByLogin(String login) {
        userRepository.findByLogin(login).map(AppUser::userId).ifPresent(this::userRemoveProcedure);
    }

    @Override
    public void removeUserByUserId(UUID userId) {
        userRepository.findById(UserIdWrapper.newOf(userId)).map(AppUser::userId).ifPresent(this::userRemoveProcedure);
    }

    @Override
    public void removeThemAll() {
        userRepository.findAll().stream().map(AppUser::userId).forEach(this::userRemoveProcedure);
    }

    @Override
    public AppUser patchEmail(EmailChangeRequest request, UserIdWrapper userIdFromAuth) {
        AppUser currentUser = userCheckBeforeModifyProperties(request.auth(), userIdFromAuth);

        if (userRepository.findByEmail(request.newEmail()).isPresent()) {
            throw new UnableToCreateException("Such email is occupied.");
        }
        return userRepository.save(AppUser.builder()
                                          .userId(currentUser.userId())
                                          .login(currentUser.login())
                                          .email(request.newEmail())
                                          .password(currentUser.password())
                                          .role(currentUser.role())
                                          .enabled(currentUser.enabled())
                                          .build());
    }

    @Override
    public AppUser patchPassword(PasswordChangeRequest request, UserIdWrapper userIdFromAuth) {
        AppUser currentUser = userCheckBeforeModifyProperties(request.auth(), userIdFromAuth);

        if (!request.firstPasswordAttempt().equals(request.secondPasswordAttempt()))
            throw new BadCredentialsException("The new passwords are not the same");

        return userRepository.save(AppUser.builder()
                                          .userId(currentUser.userId())
                                          .login(currentUser.login())
                                          .email(currentUser.email())
                                          .password(passwordEncoder.encode(request.firstPasswordAttempt()))
                                          .role(currentUser.role())
                                          .enabled(currentUser.enabled())
                                          .build());
    }

    private void userRemoveProcedure(UserIdWrapper userToRemove) {
        if (!Objects.isNull(userToRemove)) {
            userRepository.deleteById(userToRemove);
            removeUserData(userToRemove);
            registerAdminUser();
        }
    }

    private void removeUserData(UserIdWrapper userId) {
        budgetService.getAllBudgetsByUserId(userId).stream()
                     .map(Budget::budgetId).forEach(budgetService::deleteBudgetByBudgetId);
    }

    private AppUser userCheckBeforeModifyProperties(AuthenticationRequest request, UserIdWrapper userIdFromAuth) {
        String userIdFromRequest = jwtService.extractUserId(authenticationService.authenticate(request).token());
        String currentRequestUserId = userIdFromAuth.id().toString();

        if (!userIdFromRequest.equals(currentRequestUserId))
            throw new BadCredentialsException("Invalid login or password");

        return userRepository.findById(userIdFromAuth).orElseThrow(
                () -> new NoSuchElementException("There is no such user"));
    }

    @PostConstruct
    private void registerAdminUser() {
        if (!userRepository.existsByLogin("admin")) {
            AppUser admin = AppUser.builder()
                                   .userId(UserIdWrapper.newOf(UUID.randomUUID()))
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
