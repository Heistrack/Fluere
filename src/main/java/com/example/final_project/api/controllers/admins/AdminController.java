package com.example.final_project.api.controllers.admins;

import com.example.final_project.api.requests.users.admins.AdminEmailChangeRequest;
import com.example.final_project.api.requests.users.admins.AdminPasswordChangeRequest;
import com.example.final_project.api.requests.users.appusers.AuthenticationRequest;
import com.example.final_project.api.requests.users.appusers.RegisterUserRequest;
import com.example.final_project.api.responses.users.admins.AdminOperationResponse;
import com.example.final_project.api.responses.users.appusers.UserDetailsResponse;
import com.example.final_project.domain.users.admins.AdminService;
import com.example.final_project.domain.users.appusers.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.final_project.api.controllers.admins.AdminController.ADMIN_CONTROLLERS_BASE_PATH;

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

    @GetMapping("/auth-check/{login}")
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

    @PostMapping("/password-reset")
    ResponseEntity<AppUser> passwordChange(
            @RequestBody @Valid AdminPasswordChangeRequest request
    ) {
        AppUser updatedUser = adminService.resetUserPassword(request);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/email-change")
    ResponseEntity<AppUser> emailChange(
            @RequestBody @Valid AdminEmailChangeRequest request
    ) {
        AppUser updatedUser = adminService.patchUserEmail(request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/fromToken")
    ResponseEntity<AppUser> getUserFromToken(Authentication authentication) {
        return ResponseEntity.ok(adminService.findFromToken(authentication.getName()));
    }

    @GetMapping("/id/{id}")
    ResponseEntity<AppUser> getUserById(@PathVariable(name = "id") UUID userId) {
        return ResponseEntity.ok(adminService.findByUserId(userId));
    }

    @GetMapping("/logins/{login}")
    ResponseEntity<AppUser> getUserByLogin(@PathVariable(name = "login") String login) {
        return ResponseEntity.ok(adminService.findUserByLogin(login));
    }

    @GetMapping("/emails/{email}")
    ResponseEntity<AppUser> getUserByEmail(@PathVariable(name = "email") String email) {
        return ResponseEntity.ok(adminService.findUserByEmail(email));
    }

    @DeleteMapping("ids/{id}")
    ResponseEntity<UserDetailsResponse> removeUserByUserId(@PathVariable(name = "id") UUID userId) {
        adminService.removeUserByUserId(userId);
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

    @DeleteMapping("/purge-them")
    ResponseEntity<UserDetailsResponse> removeAllExceptAdmin(
            @RequestBody AuthenticationRequest confirmation
    ) {
        adminService.removeAllUsers(confirmation);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purge-them-all")
    ResponseEntity<UserDetailsResponse> removeAll(
            @RequestBody AuthenticationRequest confirmation
    ) {
        adminService.databaseRestart(confirmation);
        return ResponseEntity.noContent().build();
    }
}
