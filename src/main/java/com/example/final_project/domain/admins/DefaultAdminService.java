package com.example.final_project.domain.admins;

import com.example.final_project.api.requests.users.EmailChangeRequest;
import com.example.final_project.api.requests.users.PasswordChangeRequest;
import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.budgets.BudgetService;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.Role;
import com.example.final_project.domain.users.UserIdWrapper;
import com.example.final_project.domain.users.exceptions.UnableToCreateException;
import com.example.final_project.infrastructure.appuserrepo.AppUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultAdminService implements AdminService {
    @Value("${admin.key.value}")
    private String ADMIN_PASSWORD;

    private final AppUserRepository appUserRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BudgetService budgetService;

    @Override
    public AppUser registerNewUser(RegisterUserRequest request) {
        emailAndLoginDuplicatesCheck(request);
        return authenticationService.register(request).user();
    }

    @Override
    public UserDetailsResponse findByUserId(UUID userId) {
        return null;
    }

    @Override
    public AppUser findFromToken(String userId) {
        return null;
    }

    @Override
    public AppUser findByLogin(String login) {
        return null;
    }

    @Override
    public List<UserDetailsResponse> getAllUsers() {
        return null;
    }

    @Override
    public AppUser findUserByEmail(String email) {
        return null;
    }

    @Override
    public void removeUserByLogin(String login) {

    }

    @Override
    public void removeUserByUserId(UUID userId) {

    }

    @Override
    public void removeAllUsers() {

    }

    @Override
    public void removeThemAll() {

    }

    @Override
    public AppUser patchUserEmail(EmailChangeRequest request, UserIdWrapper userIdFromAuth) {
        return null;
    }

    @Override
    public AppUser patchUserPassword(PasswordChangeRequest request, UserIdWrapper userIdFromAuth) {
        return null;
    }

    private void emailAndLoginDuplicatesCheck(RegisterUserRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new UnableToCreateException("User's email is already occupied!");
        }
        if (appUserRepository.existsByLogin(request.login())) {
            throw new UnableToCreateException("User's login is already occupied!");
        }
    }

    @PostConstruct
    private void registerAdminUser() {
        if (!appUserRepository.existsByLogin("admin")) {
            AppUser admin = AppUser.builder()
                                   .userId(UserIdWrapper.newOf(UUID.randomUUID()))
                                   .login("admin")
                                   .email("X")
                                   .password(passwordEncoder.encode(ADMIN_PASSWORD))
                                   .role(Role.ADMIN)
                                   .enabled(true)
                                   .creationTime(LocalDateTime.now())
                                   .build();
            appUserRepository.save(admin);
        }
    }
}
