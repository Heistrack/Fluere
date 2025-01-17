package com.example.fluere.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record RegisterUserRequest(
        @Email(message = "This is not an email.")
        String email,
        @NotBlank(message = "Login can not be blank.")
        @Size(min = 3, max = 50, message = "Login can not be shorter than 3 and longer than 50 characters.")
        String login,
        @NotBlank(message = "Password can not be blank.")
        //TODO CHANGE PASSWORD SIZE - during dev make it small
        @Size(min = 1, max = 64, message = "Password can not be shorter than 8 and longer than 64 characters.")
        String password
) {
}
