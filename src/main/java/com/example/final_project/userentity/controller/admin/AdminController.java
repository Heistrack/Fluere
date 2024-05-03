package com.example.final_project.userentity.controller.admin;

import com.example.final_project.userentity.request.admin.AdminEmailChangeRequest;
import com.example.final_project.userentity.request.admin.AdminPasswordChangeRequest;
import com.example.final_project.userentity.request.appuser.AuthenticationRequest;
import com.example.final_project.userentity.request.appuser.RegisterUserRequest;
import com.example.final_project.userentity.response.admin.AdminOperationResponse;
import com.example.final_project.userentity.response.appuser.UserDetailsResponse;
import com.example.final_project.userentity.service.admin.AdminService;
import com.example.final_project.userentity.service.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @PostMapping()
    ResponseEntity<AppUser> registerNewUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        return ResponseEntity.ok(adminService.registerNewUser(request));
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
    ResponseEntity<AppUser> passwordChange(
            @RequestBody @Valid AdminPasswordChangeRequest request
    ) {
        AppUser updatedUser = adminService.resetUserPassword(request);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/email_change")
    ResponseEntity<AppUser> emailChange(
            @RequestBody @Valid AdminEmailChangeRequest request
    ) {
        AppUser updatedUser = adminService.patchUserEmail(request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/from_token")
    ResponseEntity<AppUser> getUserFromToken(Authentication authentication) {
        return ResponseEntity.ok(adminService.findFromToken(authentication.getName()));
    }

    @GetMapping("/id/{uuid}")
    ResponseEntity<AppUser> getUserById(@PathVariable(name = "uuid") UUID userUUID) {
        return ResponseEntity.ok(adminService.findByUserId(userUUID));
    }

    @GetMapping("/logins/{login}")
    ResponseEntity<AppUser> getUserByLogin(@PathVariable(name = "login") String login) {
        return ResponseEntity.ok(adminService.findUserByLogin(login));
    }

    @GetMapping("/emails/{email}")
    ResponseEntity<AppUser> getUserByEmail(@PathVariable(name = "email") String email) {
        return ResponseEntity.ok(adminService.findUserByEmail(email));
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
