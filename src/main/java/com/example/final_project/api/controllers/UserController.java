package com.example.final_project.api.controllers;

import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.ErrorDTO;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.domain.users.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.final_project.api.controllers.UserController.USERS_BASE_PATH;

@RestController
@RequestMapping(USERS_BASE_PATH)
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {
    static final String USERS_BASE_PATH = "/users";
    private final DefaultUserService userService;

    public UserController(DefaultUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    ResponseEntity<UserDetailsResponse> registerNewUser(@Valid @RequestBody RegisterUserRequest userRequestDto) {
        //todo System.out.println("halo");
        FluereAppUser user = userService.registerNewUser(userRequestDto);

        return ResponseEntity.ok(UserDetailsResponse.fromDomain(user));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UnableToRegisterException.class)
    public ResponseEntity<ErrorDTO> exceptionHandler(UnableToRegisterException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorDTO.newOf(ex.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
    }



    @GetMapping
    ResponseEntity<List<UserDetailsResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/fromToken")
    ResponseEntity<UserDetailsResponse> getSingleUser(Authentication authentication){
        return ResponseEntity.of(userService.findAppUserByName(authentication.getName()));
    }

    @GetMapping("/{userId}")
    ResponseEntity<UserDetailsResponse> getUserById(@PathVariable String userId){
        return ResponseEntity.of(userService.findAppUserByUserId(userId));
    }
}

//    @PostMapping("/login")
//    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
//    ResponseEntity<UserDetailsResponse> loginUser(Authentication authentication) {
//        FluereAppUser fluereAppUser = (FluereAppUser) authentication.getPrincipal();
//        return ResponseEntity.ok().body(UserDetailsResponse.fromDomain(fluereAppUser));
//    }

