package com.example.fluere.userentity.controller.user;

import com.example.fluere.security.request.AuthenticationRequest;
import com.example.fluere.security.request.RegisterUserRequest;
import com.example.fluere.security.response.AuthResponseDTO;
import com.example.fluere.security.response.RegisterResponseDTO;
import com.example.fluere.security.service.AuthenticationService;
import com.example.fluere.userentity.model.AppUser;
import com.example.fluere.userentity.request.appuser.EmailChangeRequest;
import com.example.fluere.userentity.request.appuser.PasswordChangeRequest;
import com.example.fluere.userentity.response.appuser.UserDetailsResponseDTO;
import com.example.fluere.userentity.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.fluere.userentity.controller.user.AppUserController.USERS_CONTROLLER_BASE_PATH;

@RestController
@RequestMapping(USERS_CONTROLLER_BASE_PATH)
@RequiredArgsConstructor
public class AppUserController {
    static final String USERS_CONTROLLER_BASE_PATH = "/api/users";
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/new_user")
    ResponseEntity<EntityModel<RegisterResponseDTO>> registerNewUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        RegisterResponseDTO registerResponseDTO = userService.registerNewUser(request);
        return ResponseEntity.status(201)
                             .body(userService.getEntityModel(registerResponseDTO, RegisterResponseDTO.class));
    }

    @PostMapping("/auth")
    ResponseEntity<EntityModel<AuthResponseDTO>> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        AuthResponseDTO authResponseDTO = authenticationService.authenticate(request);
        return ResponseEntity.ok(userService.getEntityModel(authResponseDTO, AuthResponseDTO.class));
    }

    @PostMapping("/password_change")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> passwordChange(
            @RequestBody @Valid PasswordChangeRequest request,
            Authentication authentication
    ) {
        AppUser updatedUser = userService.patchPassword(request, authentication);
        return ResponseEntity.ok(userService.getEntityModel(
                UserDetailsResponseDTO.fromDomain(updatedUser),
                UserDetailsResponseDTO.class
        ));
    }

    @PostMapping("/email_change")
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> emailChange(
            @RequestBody @Valid EmailChangeRequest request,
            Authentication authentication
    ) {
        AppUser updatedUser = userService.patchEmail(request, authentication);
        return ResponseEntity.ok(userService.getEntityModel(
                UserDetailsResponseDTO.fromDomain(updatedUser),
                UserDetailsResponseDTO.class
        ));
    }

    @GetMapping
    ResponseEntity<EntityModel<UserDetailsResponseDTO>> getMyAccountInfo(Authentication authentication) {
        AppUser user = userService.getUserDetailsFromToken(authentication);
        return ResponseEntity.ok(userService.getEntityModel(
                UserDetailsResponseDTO.fromDomain(user),
                UserDetailsResponseDTO.class
        ));
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

