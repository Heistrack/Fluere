package com.example.final_project.api.controllers.users;

import com.example.final_project.api.requests.users.appusers.AuthenticationRequest;
import com.example.final_project.api.requests.users.appusers.EmailChangeRequest;
import com.example.final_project.api.requests.users.appusers.PasswordChangeRequest;
import com.example.final_project.api.requests.users.appusers.RegisterUserRequest;
import com.example.final_project.api.responses.authentications.AuthResponseDTO;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.api.responses.users.appusers.UserDetailsResponse;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.securities.jwtauth.AuthenticationService;
import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.UserIdWrapper;
import com.example.final_project.domain.users.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.final_project.api.controllers.users.AppUserController.USERS_BASE_CONTROLLER_PATH;

@RestController
@RequestMapping(USERS_BASE_CONTROLLER_PATH)
@RequiredArgsConstructor
public class AppUserController {
    static final String USERS_BASE_CONTROLLER_PATH = "/users";
    private final UserService userService;
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


    @DeleteMapping()
    ResponseEntity<AppUser> removeOneselfAccount(
            Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        userService.removeOwnAccount(userId);
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

