package com.example.final_project.api.requests.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;


public record RegisterUserRequest(
        @Email(message = "This is not an email.")
        String email,
        @NotNull(message = "Login can not be null.")
        @NotBlank(message = "Login can not be blank.")
        @Length(min = 3, max = 50, message = "Login can not be shorter than 3 and longer than 50 characters.")
        String login,
        @NotNull(message = "Password can not be null.")
        @NotBlank(message = "Password can not be blank.")
        //TODO CHANGE PASSWORD SIZE - during dev make it small
        @Length(min = 1, max = 64, message = "Password can not be shorter than 8 and longer than 64 characters.")
        String password) {
    @Builder
    public RegisterUserRequest {
    }
}
