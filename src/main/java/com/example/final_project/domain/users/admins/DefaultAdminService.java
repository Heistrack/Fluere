package com.example.final_project.domain.users.admins;

import com.example.final_project.api.requests.users.admins.AdminEmailChangeRequest;
import com.example.final_project.api.requests.users.admins.AdminPasswordChangeRequest;
import com.example.final_project.api.requests.users.appusers.AuthenticationRequest;
import com.example.final_project.api.requests.users.appusers.RegisterUserRequest;
import com.example.final_project.api.responses.users.admins.AdminOperationResponse;
import com.example.final_project.domain.budgets.admins.AdminBudgetService;
import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.domain.users.appusers.AppUser;
import com.example.final_project.domain.users.appusers.Role;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import com.example.final_project.domain.users.appusers.exceptions.UnableToCreateException;
import com.example.final_project.infrastructure.appuserrepo.AppUserRepository;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DefaultAdminService implements AdminService {
    private final AppUserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AdminBudgetService adminBudgetService;
    @Value("${admin.key.value}")
    private String ADMIN_PASSWORD;

    @Override
    public AppUser registerNewUser(RegisterUserRequest request) {
        emailAndLoginDuplicatesCheck(request);
        return authenticationService.register(request).user();
    }

    @Override
    public AdminOperationResponse authenticateTest(String login) {
        AppUser user = userRepository.findByLogin(login).orElseThrow(
                () -> new NoSuchElementException("User doesn't exist"));
        String token = jwtService.generateToken(user);
        String message = jwtService.isTokenValid(
                token, user) ? "Authentication successful" : "Authentication failed";
        return AdminOperationResponse.newOf(message, LocalDateTime.now());
    }

    @Override
    public Page<AppUser> getAllUsersByPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public AppUser findFromToken(String userId) {
        return userRepository.findById(UserIdWrapper.newFromString(userId))
                             .orElseThrow(() -> new JwtException("Invalid token"));
    }

    @Override
    public AppUser findByUserId(UUID userId) {
        return userRepository.findById(UserIdWrapper.newOf(userId))
                             .orElseThrow(() -> new NoSuchElementException("There is no such user id!"));
    }

    @Override
    public AppUser findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                             .orElseThrow(() -> new NoSuchElementException("There is no user with such login"));
    }

    @Override
    public AppUser findUserByEmail(String email) {
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
    public void removeUserByEmail(String email) {
        userRepository.findByEmail(email).map(AppUser::userId).ifPresent(this::userRemoveProcedure);
    }


    @Override
    public void removeAllUsers(AuthenticationRequest confirmation) {
        AppUser admin = userRepository.findByLogin("admin").orElseThrow();
        UUID adminId = admin.userId().id();

        if (!(passwordEncoder.matches(confirmation.password(), admin.password()) &&
                confirmation.login().equals(admin.login()))) {
            throw new BadCredentialsException("Incorrect login or password");
        }

        List<UUID> allUsers = new ArrayList<>(
                userRepository.findAll().stream().map(AppUser::userId).map(UserIdWrapper::id).toList());
        allUsers.remove(adminId);
        for (UUID userId : allUsers) {
            removeUserByUserId(userId);
        }
    }

    @Override
    public void databaseRestart(AuthenticationRequest confirmation) {
        AppUser admin = userRepository.findByLogin("admin").orElseThrow();
        if (!(passwordEncoder.matches(confirmation.password(), admin.password()) &&
                confirmation.login().equals(admin.login()))) {
            throw new BadCredentialsException("Incorrect login or password");
        }
        userRepository.findAll().stream().map(AppUser::userId).forEach(this::userRemoveProcedure);
        userRepository.deleteById(admin.userId());
        registerAdminUser();
    }

    @Override
    public AppUser patchUserEmail(AdminEmailChangeRequest request) {
        AppUser currentUser = userRepository.findByLogin(request.login())
                                            .orElseThrow(() -> new NoSuchElementException("User doesn't exist"));

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
                                          .creationTime(currentUser.creationTime())
                                          .build());
    }


    @Override
    public AppUser resetUserPassword(AdminPasswordChangeRequest request) {
        AppUser currentUser = userRepository.findByLogin(request.login())
                                            .orElseThrow(() -> new NoSuchElementException("User doesn't exist"));

        return userRepository.save(AppUser.builder()
                                          .userId(currentUser.userId())
                                          .login(currentUser.login())
                                          .email(currentUser.email())
                                          .password(passwordEncoder.encode(request.newPassword()))
                                          .role(currentUser.role())
                                          .enabled(currentUser.enabled())
                                          .creationTime(currentUser.creationTime())
                                          .build());
    }

    private void userRemoveProcedure(UserIdWrapper userToRemove) {
        if (!Objects.isNull(userToRemove)) {
            removeUserData(userToRemove);
            userRepository.deleteById(userToRemove);
            registerAdminUser();
        }
    }

    private void removeUserData(UserIdWrapper userId) {
        adminBudgetService.getAllBudgetsByUserId(userId).stream()
                     .map(Budget::budgetId).forEach(adminBudgetService::deleteBudgetByBudgetId);
    }

    private void emailAndLoginDuplicatesCheck(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UnableToCreateException("User's email is already occupied!");
        }
        if (userRepository.existsByLogin(request.login())) {
            throw new UnableToCreateException("User's login is already occupied!");
        }
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
                                   .creationTime(LocalDateTime.now())
                                   .build();
            userRepository.save(admin);
        }
    }
}
