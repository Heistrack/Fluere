package com.example.final_project.api.controllers;

import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.domain.users.FluereAppUser;
import com.example.final_project.domain.users.UserRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.final_project.api.controllers.UserController.USERS_BASE_PATH;

@RestController
@RequestMapping(USERS_BASE_PATH)
public class UserController {

    private final UserRegistrationService registrationService;

    public UserController(UserRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    ResponseEntity<UserDetailsResponse> registerUser(@RequestBody @Valid RegisterUserRequest request){
        FluereAppUser user = registrationService.registerNewUser(request.name(), request.password(), request.email(), UserRegistrationService.defaultRoles);
        return ResponseEntity.ok().body(UserDetailsResponse.fromDomain(user));
    }



    static final String USERS_BASE_PATH = "/users";
}
