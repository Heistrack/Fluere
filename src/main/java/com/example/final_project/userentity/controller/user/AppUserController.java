package com.example.final_project.userentity.controller.user;

import com.example.final_project.expense.response.ExpenseResponseDto;
import com.example.final_project.security.request.AuthenticationRequest;
import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.security.response.AuthResponseDTO;
import com.example.final_project.security.response.RegisterResponseDTO;
import com.example.final_project.security.service.AuthenticationService;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.request.appuser.EmailChangeRequest;
import com.example.final_project.userentity.request.appuser.PasswordChangeRequest;
import com.example.final_project.userentity.response.appuser.UserDetailsResponse;
import com.example.final_project.userentity.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.final_project.userentity.controller.user.AppUserController.USERS_CONTROLLER_BASE_PATH;

@RestController
@RequestMapping(USERS_CONTROLLER_BASE_PATH)
@RequiredArgsConstructor
public class AppUserController {
    static final String USERS_CONTROLLER_BASE_PATH = "/api/users";
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/new_user")
    ResponseEntity<RegisterResponseDTO> registerNewUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        return ResponseEntity.status(201).body(userService.registerNewUser(request));
    }

    @PostMapping("/auth")
    ResponseEntity<AuthResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/password_change")
    ResponseEntity<UserDetailsResponse> passwordChange(
            @RequestBody @Valid PasswordChangeRequest request,
            Authentication authentication
    ) {
        AppUser updatedUser = userService.patchPassword(request, authentication);
        return ResponseEntity.ok(UserDetailsResponse.fromDomain(updatedUser));
    }

    @PostMapping("/email_change")
    ResponseEntity<UserDetailsResponse> emailChange(
            @RequestBody @Valid EmailChangeRequest request,
            Authentication authentication
    ) {
        AppUser updatedUser = userService.patchEmail(request, authentication);
        return ResponseEntity.ok(UserDetailsResponse.fromDomain(updatedUser));
    }

    @GetMapping
    ResponseEntity<EntityModel<AppUser>> getMyAccountInfo(Authentication authentication) {
        AppUser user = userService.getUserDetailsFromToken(authentication);
        return ResponseEntity.ok(userService.getEntityModel(user));
    }


    @DeleteMapping()
    ResponseEntity<EntityModel<AppUser>> removeOneselfAccount(
            @RequestBody AuthenticationRequest confirmation,
            Authentication authentication
    ) {
        userService.removeOwnAccount(confirmation, authentication);
        return ResponseEntity.noContent().build();
    }
}

