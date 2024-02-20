package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.appusers.AuthenticationRequest;
import com.example.final_project.api.requests.users.appusers.EmailChangeRequest;
import com.example.final_project.api.requests.users.appusers.PasswordChangeRequest;
import com.example.final_project.api.requests.users.appusers.RegisterUserRequest;
import com.example.final_project.api.responses.users.appusers.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetService;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.domain.users.exceptions.UnableToCreateException;
import com.example.final_project.infrastructure.appuserrepo.AppUserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
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
    private final AppUserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BudgetService budgetService;

    @Override
    public RegisterResponseDTO registerNewUser(RegisterUserRequest request) {
        emailAndLoginDuplicatesCheck(request);

        return authenticationService.register(request);
    }

    @Override
    public AppUser findFromToken(String userId) {
        //TODO Move this method for admin only
        return userRepository.findById(UserIdWrapper.newFromString(userId))
                             .orElseThrow(() -> new JwtException("Invalid token"));
    }

    @Override
    public List<UserDetailsResponse> getAllUsers() {
        //TODO move to admin service
        return userRepository.findAll()
                             .stream()
                             .map(UserDetailsResponse::fromDomain).toList();
    }

    @Override
    public UserDetailsResponse findByUserId(UUID userId) {
        //TODO remove to admin service
        return userRepository.findById(UserIdWrapper.newOf(userId))
                             .map(UserDetailsResponse::fromDomain)
                             .orElseThrow(() -> new NoSuchElementException("There is no such user id!"));
    }

    @Override
    public AppUser findByLogin(String login) {
        //TODO remove to admin service
        return userRepository.findByLogin(login)
                             .orElseThrow(() -> new NoSuchElementException("There is no user with such login"));
    }

    @Override
    public AppUser findByEmail(String email) {
        //TODO remove to admin service
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new NoSuchElementException("There is no user with such email"));
    }

    @Override
    public void removeMyAccount(String login) {
        //TODO let it be but it should remove only auth account
        userRepository.findByLogin(login).map(AppUser::userId).ifPresent(this::userRemoveProcedure);
    }

    @Override
    public void removeUserByUserId(UUID userId) {
        //TODO remove to admin service
        userRepository.findById(UserIdWrapper.newOf(userId)).map(AppUser::userId).ifPresent(this::userRemoveProcedure);
    }

    @Override
    public void removeThemAll() {
        //TODO remove to admin service
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
                                          .creationTime(currentUser.creationTime())
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
                                          .creationTime(currentUser.creationTime())
                                          .build());
    }

    private void userRemoveProcedure(UserIdWrapper userToRemove) {
        if (!Objects.isNull(userToRemove)) {
            userRepository.deleteById(userToRemove);
            removeUserData(userToRemove);
        }
    }

    private void removeUserData(UserIdWrapper userId) {
        budgetService.getAllBudgetsByUserId(userId).stream()
                     .map(Budget::budgetId).forEach(budgetService::deleteBudgetByBudgetId);
    }

    private void emailAndLoginDuplicatesCheck(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UnableToCreateException("User's email is already occupied!");
        }
        if (userRepository.existsByLogin(request.login())) {
            throw new UnableToCreateException("User's login is already occupied!");
        }
    }

    private AppUser userCheckBeforeModifyProperties(AuthenticationRequest request, UserIdWrapper userIdFromAuth) {
        String userIdFromRequest = jwtService.extractUserId(authenticationService.authenticate(request).token());
        String currentRequestUserId = userIdFromAuth.id().toString();

        if (!userIdFromRequest.equals(currentRequestUserId))
            throw new BadCredentialsException("Invalid login or newPassword");

        return userRepository.findById(userIdFromAuth).orElseThrow(
                () -> new NoSuchElementException("There is no such user"));
    }
}
