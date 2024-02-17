package com.example.final_project.api.controllers;

import com.example.final_project.api.requests.users.AuthenticationRequest;
import com.example.final_project.api.requests.users.EmailChangeRequest;
import com.example.final_project.api.requests.users.PasswordChangeRequest;
import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.AuthResponseDTO;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.DefaultUserService;
import com.example.final_project.domain.users.UserIdWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.final_project.api.controllers.UserController.USERS_BASE_CONTROLLER_PATH;

@RestController
@RequestMapping(USERS_BASE_CONTROLLER_PATH)
@RequiredArgsConstructor
public class UserController {
    static final String USERS_BASE_CONTROLLER_PATH = "/users";
    private final DefaultUserService userService;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping()
    ResponseEntity<RegisterResponseDTO> registerNewUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        return ResponseEntity.ok(userService.registerNewUser(request));
    }

    @PostMapping("/auth")
    ResponseEntity<AuthResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping
    ResponseEntity<List<UserDetailsResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/fromToken")
    ResponseEntity<AppUser> getUserFromToken(Authentication authentication) {
        return ResponseEntity.ok(userService.findFromToken(authentication.getName()));
    }

    @GetMapping("/id/{id}")
    ResponseEntity<UserDetailsResponse> getUserById(@PathVariable(name = "id") UUID userId) {
        return ResponseEntity.ok(userService.findByUserId(userId));
    }

    @PostMapping("/logins")
    ResponseEntity<AppUser> getUserByLogin(@RequestBody Map<String, String> loginMap) {
        return ResponseEntity.ok(userService.findByLogin(loginMap.get("login")));
    }

    @PostMapping("/emails")
    ResponseEntity<AppUser> getUserByEmail(@RequestBody Map<String, String> emailMap) {
        return ResponseEntity.ok(userService.findByEmail(emailMap.get("email")));
    }

    @DeleteMapping()
    ResponseEntity<AppUser> removeUserByLogin(@RequestBody Map<String, String> loginMap) {
        userService.removeUserByLogin(loginMap.get("login"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<UserDetailsResponse> removeUserByUserId(@PathVariable(name = "id") UUID userId) {
        userService.removeUserByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purge-them-all")
    ResponseEntity<UserDetailsResponse> removeAll() {
        userService.removeThemAll();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login-change")
    ResponseEntity<UserDetailsResponse> passwordChange(
            @RequestBody @Valid PasswordChangeRequest request,
            Authentication authentication
    ) {
        UserIdWrapper userIdFromToken = jwtService.extractUserIdFromRequestAuth(authentication);

        AppUser updatedUser = userService.patchPassword(request, userIdFromToken);

        return ResponseEntity.ok(UserDetailsResponse.fromDomain(updatedUser));
    }

    @PostMapping("/email-change")
    ResponseEntity<UserDetailsResponse> emailChange(
            @RequestBody @Valid EmailChangeRequest request,
            Authentication authentication
    ) {
        UserIdWrapper userIdWrapper = jwtService.extractUserIdFromRequestAuth(authentication);

        AppUser updatedUser = userService.patchEmail(request, userIdWrapper);

        return ResponseEntity.ok(UserDetailsResponse.fromDomain(updatedUser));
    }
}

