package com.example.final_project.api.controllers.users;

import com.example.final_project.api.requests.users.appusers.AuthenticationRequest;
import com.example.final_project.api.requests.users.appusers.EmailChangeRequest;
import com.example.final_project.api.requests.users.appusers.PasswordChangeRequest;
import com.example.final_project.api.requests.users.appusers.RegisterUserRequest;
import com.example.final_project.api.responses.authentications.AuthResponseDTO;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.api.responses.users.appusers.UserDetailsResponse;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.domain.users.appusers.AppUser;
import com.example.final_project.domain.users.appusers.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.final_project.api.controllers.users.AppUserController.USERS_CONTROLLER_BASE_PATH;

@RestController
@RequestMapping(USERS_CONTROLLER_BASE_PATH)
@RequiredArgsConstructor
public class AppUserController {
    static final String USERS_CONTROLLER_BASE_PATH = "/api/users";
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/new-user")
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

    @PostMapping("/password-change")
    ResponseEntity<UserDetailsResponse> passwordChange(
            @RequestBody @Valid PasswordChangeRequest request,
            Authentication authentication
    ) {
        AppUser updatedUser = userService.patchPassword(request, authentication);
        return ResponseEntity.ok(UserDetailsResponse.fromDomain(updatedUser));
    }

    @PostMapping("/email-change")
    ResponseEntity<UserDetailsResponse> emailChange(
            @RequestBody @Valid EmailChangeRequest request,
            Authentication authentication
    ) {
        AppUser updatedUser = userService.patchEmail(request, authentication);
        return ResponseEntity.ok(UserDetailsResponse.fromDomain(updatedUser));
    }

    @GetMapping
    ResponseEntity<AppUser> getMyAccountInfo(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserDetailsFromToken(authentication));
    }


    @DeleteMapping()
    ResponseEntity<AppUser> removeOneselfAccount(
            Authentication authentication
    ) {
        userService.removeOwnAccount(authentication);
        return ResponseEntity.noContent().build();
    }
}

