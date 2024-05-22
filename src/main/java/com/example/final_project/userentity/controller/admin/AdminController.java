package com.example.final_project.userentity.controller.admin;

import com.example.final_project.security.request.AuthenticationRequest;
import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.request.admin.AdminEmailChangeRequest;
import com.example.final_project.userentity.request.admin.AdminPasswordChangeRequest;
import com.example.final_project.userentity.response.admin.AdminOperationResponse;
import com.example.final_project.userentity.response.appuser.UserDetailsResponse;
import com.example.final_project.userentity.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
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
    private final AdminService adminService;
//TODO add hateoas to admin and user layer

    @PostMapping()
    ResponseEntity<EntityModel<AppUser>> registerNewUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        AppUser appUser = adminService.registerNewUser(request);
        return ResponseEntity.status(201).body(adminService.getEntityModel(appUser));
    }

    @GetMapping("/auth_check/{login}")
    ResponseEntity<AdminOperationResponse> authenticate(
            @PathVariable(name = "login") String login
    ) {
        return ResponseEntity.ok(adminService.authenticateTest(login));
    }

    @GetMapping
    ResponseEntity<Page<UserDetailsResponse>> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        return ResponseEntity.ok(
                adminService.getAllUsersByPage(PageRequest.of(page, size, Sort.by(sortDirection, sortBy)))
                            .map(UserDetailsResponse::fromDomain));
    }

    @PostMapping("/password_reset")
    ResponseEntity<EntityModel<AppUser>> passwordChange(
            @RequestBody @Valid AdminPasswordChangeRequest request
    ) {
        AppUser updatedUser = adminService.resetUserPassword(request);
        return ResponseEntity.ok(adminService.getEntityModel(updatedUser));
    }

    @PostMapping("/email_change")
    ResponseEntity<EntityModel<AppUser>> emailChange(
            @RequestBody @Valid AdminEmailChangeRequest request
    ) {
        AppUser updatedUser = adminService.patchUserEmail(request);
        return ResponseEntity.ok(adminService.getEntityModel(updatedUser));
    }

    @GetMapping("/from_token")
    ResponseEntity<EntityModel<AppUser>> getUserFromToken(Authentication authentication) {
        AppUser user = adminService.findFromToken(authentication.getName());
        return ResponseEntity.ok(adminService.getEntityModel(user));
    }

    @GetMapping("/id/{uuid}")
    ResponseEntity<EntityModel<AppUser>> getUserById(@PathVariable(name = "uuid") UUID userUUID) {
        AppUser user = adminService.findByUserId(userUUID);
        return ResponseEntity.ok(adminService.getEntityModel(user));
    }

    @GetMapping("/logins/{login}")
    ResponseEntity<EntityModel<AppUser>> getUserByLogin(@PathVariable(name = "login") String login) {
        AppUser user = adminService.findUserByLogin(login);
        return ResponseEntity.ok(adminService.getEntityModel(user));
    }

    @GetMapping("/emails/{email}")
    ResponseEntity<EntityModel<AppUser>> getUserByEmail(@PathVariable(name = "email") String email) {
        AppUser user = adminService.findUserByEmail(email);
        return ResponseEntity.ok(adminService.getEntityModel(user));
    }

    @DeleteMapping("ids/{uuid}")
    ResponseEntity<UserDetailsResponse> removeUserByUserId(@PathVariable(name = "uuid") UUID userUUID) {
        adminService.removeUserByUserId(userUUID);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{email}")
    ResponseEntity<UserDetailsResponse> removeUserByEmail(@PathVariable(name = "email") String email) {
        adminService.removeUserByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("logins/{login}")
    ResponseEntity<AdminOperationResponse> removeUserByLogin(@PathVariable(name = "login") String login) {
        adminService.removeUserByLogin(login);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purge_them")
    ResponseEntity<UserDetailsResponse> removeAllExceptAdmin(
            @RequestBody AuthenticationRequest confirmation
    ) {
        adminService.removeAllUsers(confirmation);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purge_them_all")
    ResponseEntity<UserDetailsResponse> removeAll(
            @RequestBody AuthenticationRequest confirmation
    ) {
        adminService.databaseRestart(confirmation);
        return ResponseEntity.noContent().build();
    }
}
