package com.example.final_project.userentity.service.admin;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.budget.service.admin.AdminBudgetService;
import com.example.final_project.exception.custom.UnableToCreateException;
import com.example.final_project.security.request.AuthenticationRequest;
import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.security.service.AuthenticationService;
import com.example.final_project.security.service.JwtService;
import com.example.final_project.userentity.controller.admin.AdminController;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.model.Role;
import com.example.final_project.userentity.model.UserIdWrapper;
import com.example.final_project.userentity.repository.AppUserRepository;
import com.example.final_project.userentity.request.admin.AdminEmailChangeRequest;
import com.example.final_project.userentity.request.admin.AdminPasswordChangeRequest;
import com.example.final_project.userentity.response.admin.AdminOperationResponse;
import com.example.final_project.userentity.service.user.UserInnerServiceLogic;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class DefaultAdminService implements AdminService {
    private final AppUserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final UserInnerServiceLogic innerServiceLogic;
    private final AdminBudgetService adminBudgetService;
    private final JwtService jwtService;
    @Value("${admin.key.value}")
    private String ADMIN_PASSWORD;

    @Override
    public AppUser registerNewUser(RegisterUserRequest request) {
        innerServiceLogic.emailAndLoginDuplicatesCheck(request);
        return authenticationService.register(request).getUser();
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
        userRepository.findByLogin(login).map(AppUser::getUserId).ifPresent(this::userRemoveProcedure);
    }

    @Override
    public void removeUserByUserId(UUID userId) {
        userRepository.findById(UserIdWrapper.newOf(userId)).map(AppUser::getUserId)
                      .ifPresent(this::userRemoveProcedure);
    }

    @Override
    public void removeUserByEmail(String email) {
        userRepository.findByEmail(email).map(AppUser::getUserId).ifPresent(this::userRemoveProcedure);
    }


    @Override
    public void removeAllUsers(AuthenticationRequest confirmation) {
        AppUser admin = userRepository.findByLogin("admin").orElseThrow();
        UUID adminId = admin.getUserId().id();

        if (!(passwordEncoder.matches(confirmation.password(), admin.getPassword()) &&
                confirmation.login().equals(admin.getLogin()))) {
            throw new BadCredentialsException("Incorrect login or password");
        }

        List<UUID> allUsers = new ArrayList<>(
                userRepository.findAll().stream().map(AppUser::getUserId).map(UserIdWrapper::id).toList());
        allUsers.remove(adminId);
        for (UUID userId : allUsers) {
            removeUserByUserId(userId);
        }
    }

    @Override
    public void databaseRestart(AuthenticationRequest confirmation) {
        AppUser admin = userRepository.findByLogin("admin").orElseThrow();
        if (!(passwordEncoder.matches(confirmation.password(), admin.getPassword()) &&
                confirmation.login().equals(admin.getLogin()))) {
            throw new BadCredentialsException("Incorrect login or password");
        }
        userRepository.findAll().stream().map(AppUser::getUserId).forEach(this::userRemoveProcedure);
        userRepository.deleteById(admin.getUserId());
        registerAdminUser();
    }

    @Override
    public AppUser patchUserEmail(AdminEmailChangeRequest request) {
        AppUser currentUser = userRepository.findByLogin(request.login())
                                            .orElseThrow(() -> new NoSuchElementException("User doesn't exist"));

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
    public AppUser resetUserPassword(AdminPasswordChangeRequest request) {
        AppUser currentUser = userRepository.findByLogin(request.login())
                                            .orElseThrow(() -> new NoSuchElementException("User doesn't exist"));

        return userRepository.save(AppUser.newOf(
                currentUser.getUserId(),
                currentUser.getLogin(),
                currentUser.getEmail(),
                passwordEncoder.encode(request.newPassword()),
                currentUser.getRole(),
                currentUser.getEnabled(),
                currentUser.getCreationTime()
        ));
    }

    @Override
    public <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast) {
        Link link = linkTo(AdminController.class).slash(linkableDTO.PathMessage()).withSelfRel();
        linkableDTO.addLink(link);
        return EntityModel.of(classCast.cast(linkableDTO));
    }

    @Override
    public <T extends LinkableDTO> PagedModel<T> getEntities(Page<T> linkableDTOs, Class<T> classCast) {
        Link generalLink = linkTo(AdminController.class).withSelfRel();
        return innerServiceLogic.getPagedModel(linkableDTOs, classCast, generalLink);
    }

    @PostConstruct
    private void registerAdminUser() {
        if (!userRepository.existsByLogin("admin")) {
            AppUser admin = AppUser.newOf(
                    UserIdWrapper.newOf(UUID.randomUUID()),
                    "admin",
                    "X",
                    passwordEncoder.encode(ADMIN_PASSWORD),
                    Role.ADMIN,
                    true,
                    LocalDateTime.now()
            );

            userRepository.save(admin);
        }
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
}
