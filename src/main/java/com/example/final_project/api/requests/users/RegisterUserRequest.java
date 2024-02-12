package com.example.final_project.api.requests.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;


public record RegisterUserRequest(
        @Email
        String email,
        @NotNull(message = "login cannot be null")
        @NotBlank(message = "login cannot be blank")
        @Length(min = 3, max = 50, message = "login cannot be shorter than 3 and longer than 50")
        String login,
        @NotNull(message = "password cannot be null")
        @NotBlank(message = "password cannot be blank")
        //TODO CHANGE PASSWORD SIZE - during dev make it small
        @Length(min = 1, max = 64, message = "password cannot be shorter than 8 and longer than 20")
        String password) {
        @Builder
        public RegisterUserRequest {
        }
}
