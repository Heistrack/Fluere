package com.example.final_project.api.controllers.admins;

import com.example.final_project.api.requests.users.AuthenticationRequest;
import com.example.final_project.api.requests.users.EmailChangeRequest;
import com.example.final_project.api.requests.users.PasswordChangeRequest;
import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.AuthResponseDTO;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.admins.AdminService;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.UserIdWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.final_project.api.controllers.admins.AdminController.ADMIN_BASE_CONTROLLER_PATH;

@RestController
@RequestMapping(ADMIN_BASE_CONTROLLER_PATH)
@RequiredArgsConstructor
public class AdminController {
    static final String ADMIN_BASE_CONTROLLER_PATH = "/admin/users";
    private final AdminService adminService;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping()
    ResponseEntity<AppUser> registerNewUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        return ResponseEntity.ok(adminService.registerNewUser(request));
    }

    @PostMapping("/auth")
    ResponseEntity<AuthResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        //TODO should return boolean for test if auth is correct
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping
    ResponseEntity<List<AppUser>> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        //TODO should return pagable list of all users as short DTO. For more details
        //TODO should once use more specified methods
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/fromToken")
    ResponseEntity<AppUser> getUserFromToken(Authentication authentication) {
        //TODO Should return userDetails for admin for tests
        return ResponseEntity.ok(adminService.findFromToken(authentication.getName()));
    }

    @GetMapping("/id/{id}")
    ResponseEntity<AppUser> getUserById(@PathVariable(name = "id") UUID userId) {

        return ResponseEntity.ok(adminService.findByUserId(userId));
    }

    @PostMapping("/logins")
    ResponseEntity<AppUser> getUserByLogin(@RequestBody Map<String, String> loginMap) {
        //TODO should return user by login with details
        return ResponseEntity.ok(adminService.findByLogin(loginMap.get("login")));
    }

    @PostMapping("/emails")
    ResponseEntity<AppUser> getUserByEmail(@RequestBody Map<String, String> emailMap) {
        return ResponseEntity.ok(adminService.findByEmail(emailMap.get("email")));
    }

    @DeleteMapping()
    ResponseEntity<AppUser> removeUserByLogin(@RequestBody Map<String, String> loginMap) {
        //TODO because it's a admin controller we should get info from db if delete was
        //TODO successful
        adminService.removeUserByLogin(loginMap.get("login"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<UserDetailsResponse> removeUserByUserId(@PathVariable(name = "id") UUID userId) {
        adminService.removeUserByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{email}")
    ResponseEntity<UserDetailsResponse> removeUserByEmail(@PathVariable(name = "email") String email) {
        //TODO should remove users by email
        adminService.removeUserByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purge-them")
    ResponseEntity<UserDetailsResponse> removeAllExceptAdmin() {
        //TODO should remove all users except admin
        adminService.removeThemAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purge-them-all")
    ResponseEntity<UserDetailsResponse> removeAll() {
        //TODO should remove all users including admin
        adminService.removeThemAll();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login-change")
    ResponseEntity<UserDetailsResponse> passwordChange(
            @RequestBody @Valid PasswordChangeRequest request,
            Authentication authentication
    ) {
        //TODO should allow to change every user login
        UserIdWrapper userIdFromToken = jwtService.extractUserIdFromRequestAuth(authentication);

        AppUser updatedUser = adminService.patchPassword(request, userIdFromToken);

        return ResponseEntity.ok(UserDetailsResponse.fromDomain(updatedUser));
    }

    @PostMapping("/email-change")
    ResponseEntity<UserDetailsResponse> emailChange(
            @RequestBody @Valid EmailChangeRequest request,
            Authentication authentication
    ) {
        //TODO should allow to change every user email

        UserIdWrapper userIdWrapper = jwtService.extractUserIdFromRequestAuth(authentication);

        AppUser updatedUser = adminService.patchEmail(request, userIdWrapper);

        return ResponseEntity.ok(UserDetailsResponse.fromDomain(updatedUser));
    }
}
