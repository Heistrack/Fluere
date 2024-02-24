package com.example.final_project.domain.users.appusers;

import com.example.final_project.api.requests.users.appusers.AuthenticationRequest;
import com.example.final_project.api.requests.users.appusers.EmailChangeRequest;
import com.example.final_project.api.requests.users.appusers.PasswordChangeRequest;
import com.example.final_project.api.requests.users.appusers.RegisterUserRequest;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetService;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.domain.users.appusers.exceptions.UnableToCreateException;
import com.example.final_project.infrastructure.appuserrepo.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

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
    public AppUser getUserDetailsFromToken(Authentication authentication) {
        UserIdWrapper userIdWrapper = jwtService.extractUserIdFromRequestAuth(authentication);
        return userRepository.findById(userIdWrapper)
                             .orElseThrow(() -> new NoSuchElementException("No user has been found"));
    }


    @Override
    public void removeOwnAccount(Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        if (userRepository.findById(userId).map(AppUser::userId).isPresent()) {
            userRemoveProcedure(authentication);
        }
    }

    @Override
    public AppUser patchEmail(EmailChangeRequest request, Authentication authentication) {
        UserIdWrapper userIdFromAuth = jwtService.extractUserIdFromRequestAuth(authentication);
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
    public AppUser patchPassword(PasswordChangeRequest request, Authentication authentication) {
        UserIdWrapper userIdFromAuth = jwtService.extractUserIdFromRequestAuth(authentication);
        AppUser currentUser = userCheckBeforeModifyProperties(request.auth(), userIdFromAuth);

        if (!request.firstPasswordAttempt().equals(request.secondPasswordAttempt()))
            throw new BadCredentialsException("Passwords are not the same");

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

    private void userRemoveProcedure(Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        removeUserData(authentication);
        userRepository.deleteById(userId);
    }

    private void removeUserData(Authentication authentication) {
        budgetService.getAllBudgetsByUserId(authentication).stream()
                     .map(Budget::budgetId)
                     .forEach((budgetId) -> budgetService.deleteAllBudgetExpensesByBudgetId(budgetId, authentication));
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
            throw new BadCredentialsException("Invalid login or password");

        return userRepository.findById(userIdFromAuth).orElseThrow(
                () -> new NoSuchElementException("There is no such user"));
    }
}
