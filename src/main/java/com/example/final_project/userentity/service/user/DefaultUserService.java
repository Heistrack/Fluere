package com.example.final_project.userentity.service.user;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.service.user.BudgetService;
import com.example.final_project.exception.custom.UnableToCreateException;
import com.example.final_project.security.request.AuthenticationRequest;
import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.security.response.RegisterResponseDTO;
import com.example.final_project.security.service.AuthenticationService;
import com.example.final_project.security.service.JwtService;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.model.UserIdWrapper;
import com.example.final_project.userentity.repository.AppUserRepository;
import com.example.final_project.userentity.request.appuser.EmailChangeRequest;
import com.example.final_project.userentity.request.appuser.PasswordChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

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
    public void removeOwnAccount(AuthenticationRequest confirmation,
                                 Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Optional<AppUser> user = userRepository.findById(userId);

        if (!(user.isPresent() && passwordEncoder.matches(confirmation.password(), user.get().password()) &&
                confirmation.login().equals(user.get().login()))) {
            throw new BadCredentialsException("Login or password are incorrect");
        }

        if (!user.get().login().equals("admin")) {
            userRemoveProcedure(authentication);
        }
    }

    @Override
    public AppUser patchEmail(EmailChangeRequest request, Authentication authentication) {
        AppUser currentUser = userCheckBeforeModifyProperties(request.password(), authentication);

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
        AppUser currentUser = userCheckBeforeModifyProperties(request.oldPassword(), authentication);

        if (!request.firstPasswordAttempt().equals(request.secondPasswordAttempt()))
            throw new BadCredentialsException("Passwords are not the same.");

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

    private AppUser userCheckBeforeModifyProperties(String password, Authentication authentication) {
        AppUser userFromAuth = (AppUser) authentication.getPrincipal();

        if (!passwordEncoder.matches(password, userFromAuth.password()))
            throw new BadCredentialsException("Invalid login or password");

        return userRepository.findById(userFromAuth.userId()).orElseThrow(
                () -> new NoSuchElementException("There is no such user"));
    }
}
