package com.example.fluere.userentity.service.user;

import com.example.fluere.budget.model.Budget;
import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.budget.service.user.BudgetService;
import com.example.fluere.exception.custom.UnableToCreateException;
import com.example.fluere.security.request.AuthenticationRequest;
import com.example.fluere.security.request.RegisterUserRequest;
import com.example.fluere.security.response.RegisterResponseDTO;
import com.example.fluere.security.service.AuthenticationService;
import com.example.fluere.security.service.JwtService;
import com.example.fluere.userentity.controller.user.AppUserController;
import com.example.fluere.userentity.model.AppUser;
import com.example.fluere.userentity.model.UserIdWrapper;
import com.example.fluere.userentity.repository.AppUserRepository;
import com.example.fluere.userentity.request.appuser.EmailChangeRequest;
import com.example.fluere.userentity.request.appuser.PasswordChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {
    private final AppUserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final UserInnerServiceLogic innerServiceLogic;
    private final JwtService jwtService;
    private final BudgetService budgetService;

    @Override
    public RegisterResponseDTO registerNewUser(RegisterUserRequest request) {
        innerServiceLogic.emailAndLoginDuplicatesCheck(request);
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

        if (!(user.isPresent() && passwordEncoder.matches(confirmation.password(), user.get().getPassword()) &&
                confirmation.login().equals(user.get().getLogin()))) {
            throw new BadCredentialsException("Login or password are incorrect");
        }

        if (!user.get().getLogin().equals("admin")) {
            userRemoveProcedure(authentication);
        }
    }

    @Override
    public AppUser patchEmail(EmailChangeRequest request, Authentication authentication) {
        AppUser currentUser = userCheckBeforeModifyProperties(request.password(), authentication);

        if (userRepository.findByEmail(request.newEmail()).isPresent()) {
            throw new UnableToCreateException("Such email is occupied.");
        }

        return userRepository.save(AppUser.newOf(
                currentUser.getUserId(),
                currentUser.getLogin(),
                request.newEmail(),
                currentUser.getPassword(),
                currentUser.getRole(),
                currentUser.getEnabled(),
                currentUser.getCreationTime()
        ));
    }

    @Override
    public AppUser patchPassword(PasswordChangeRequest request, Authentication authentication) {
        AppUser currentUser = userCheckBeforeModifyProperties(request.oldPassword(), authentication);

        if (!request.firstPasswordAttempt().equals(request.secondPasswordAttempt()))
            throw new BadCredentialsException("Passwords are not the same.");

        return userRepository.save(AppUser.newOf(
                currentUser.getUserId(),
                currentUser.getLogin(),
                currentUser.getEmail(),
                passwordEncoder.encode(request.firstPasswordAttempt()),
                currentUser.getRole(),
                currentUser.getEnabled(),
                currentUser.getCreationTime()
        ));
    }

    @Override
    public <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast) {
        Link link = linkTo(AppUserController.class).slash(linkableDTO.PathMessage()).withSelfRel();
        linkableDTO.addLink(link);
        return EntityModel.of(classCast.cast(linkableDTO));
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

    private AppUser userCheckBeforeModifyProperties(String password, Authentication authentication) {
        AppUser userFromAuth = (AppUser) authentication.getPrincipal();

        if (!passwordEncoder.matches(password, userFromAuth.getPassword()))
            throw new BadCredentialsException("Invalid login or password");

        return userRepository.findById(userFromAuth.getUserId()).orElseThrow(
                () -> new NoSuchElementException("There is no such user"));
    }
}
