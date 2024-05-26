package com.example.final_project.userentity.controller.admin;

import com.example.final_project.security.request.AuthenticationRequest;
import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.request.admin.AdminEmailChangeRequest;
import com.example.final_project.userentity.request.admin.AdminPasswordChangeRequest;
import com.example.final_project.userentity.response.admin.AdminOperationResponse;
import com.example.final_project.userentity.response.appuser.UserDetailsResponseDTO;
import com.example.final_project.userentity.service.admin.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.final_project.userentity.controller.admin.AdminController.ADMIN_CONTROLLERS_BASE_PATH;

@RestController
@RequestMapping(ADMIN_CONTROLLERS_BASE_PATH)
@RequiredArgsConstructor
public class AdminController {
    static final String ADMIN_CONTROLLERS_BASE_PATH = "/api/x/users";
    private final AdminUserService adminUserService;

    @PostMapping()
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> registerNewUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        AppUser appUser = adminUserService.registerNewUser(request);
        return ResponseEntity.status(201).body(adminUserService.getEntityModel(UserDetailsResponseDTO.fromDomain(appUser),
                                                                               UserDetailsResponseDTO.class
        ));
    }

    @GetMapping("/auth_check/{login}")
    ResponseEntity<EntityModel<AdminOperationResponse>> authenticate(
            @PathVariable(name = "login") String login
    ) {
        AdminOperationResponse adminOperationResponse = adminUserService.authenticateTest(login);
        return ResponseEntity.ok(adminUserService.getEntityModel(adminOperationResponse, AdminOperationResponse.class));
    }

    @GetMapping
    ResponseEntity<PagedModel<UserDetailsResponseDTO>> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        Page<UserDetailsResponseDTO> allByPage = adminUserService.getAllUsersByPage(
                                                                     PageRequest.of(page, size, Sort.by(sortDirection, sortBy)))
                                                                 .map(UserDetailsResponseDTO::fromDomain);
        return ResponseEntity.ok(adminUserService.getEntities(allByPage, UserDetailsResponseDTO.class));
    }

    @PostMapping("/password_reset")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> passwordChange(
            @RequestBody @Valid AdminPasswordChangeRequest request
    ) {
        AppUser updatedUser = adminUserService.resetUserPassword(request);
        return ResponseEntity.ok(adminUserService.getEntityModel(UserDetailsResponseDTO.fromDomain(updatedUser),
                                                                 UserDetailsResponseDTO.class
        ));
    }

    @PostMapping("/email_change")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> emailChange(
            @RequestBody @Valid AdminEmailChangeRequest request
    ) {
        AppUser updatedUser = adminUserService.patchUserEmail(request);
        return ResponseEntity.ok(adminUserService.getEntityModel(UserDetailsResponseDTO.fromDomain(updatedUser),
                                                                 UserDetailsResponseDTO.class
        ));
    }

    @GetMapping("/from_token")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> getUserFromToken(Authentication authentication) {
        AppUser user = adminUserService.getFromToken(authentication.getName());
        return ResponseEntity.ok(
                adminUserService.getEntityModel(UserDetailsResponseDTO.fromDomain(user), UserDetailsResponseDTO.class));
    }

    @GetMapping("/id/{uuid}")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> getUserById(@PathVariable(name = "uuid") UUID userUUID) {
        AppUser user = adminUserService.getByUserId(userUUID);
        return ResponseEntity.ok(
                adminUserService.getEntityModel(UserDetailsResponseDTO.fromDomain(user), UserDetailsResponseDTO.class));
    }

    @GetMapping("/logins/{login}")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> getUserByLogin(@PathVariable(name = "login") String login) {
        AppUser user = adminUserService.getUserByLogin(login);
        return ResponseEntity.ok(
                adminUserService.getEntityModel(UserDetailsResponseDTO.fromDomain(user), UserDetailsResponseDTO.class));
    }

    @GetMapping("/emails/{email}")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> getUserByEmail(@PathVariable(name = "email") String email) {
        AppUser user = adminUserService.getUserByEmail(email);
        return ResponseEntity.ok(
                adminUserService.getEntityModel(UserDetailsResponseDTO.fromDomain(user), UserDetailsResponseDTO.class));
    }

    @DeleteMapping("ids/{uuid}")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> removeUserByUserId(@PathVariable(name = "uuid") UUID userUUID) {
        adminUserService.removeUserByUserId(userUUID);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{email}")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> removeUserByEmail(@PathVariable(name = "email") String email) {
        adminUserService.removeUserByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("logins/{login}")
    ResponseEntity<EntityModel<AdminOperationResponse>> removeUserByLogin(@PathVariable(name = "login") String login) {
        adminUserService.removeUserByLogin(login);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purge_them")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> removeAllExceptAdmin(
            @RequestBody AuthenticationRequest confirmation
    ) {
        adminUserService.removeAllUsers(confirmation);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purge_them_all")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> removeAll(
            @RequestBody AuthenticationRequest confirmation
    ) {
        adminUserService.databaseRestart(confirmation);
        return ResponseEntity.noContent().build();
    }
}
