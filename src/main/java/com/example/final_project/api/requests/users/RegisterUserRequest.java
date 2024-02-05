package com.example.final_project.api.requests.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;


public record RegisterUserRequest(
        @NotNull(message = "name cannot be null")
        @NotBlank(message = "name cannot be blank")
        @Length(min = 3, max = 50, message = "name cannot be shorter than 3 and longer than 50")
        String name,
        @NotNull(message = "password cannot be null")
        @NotBlank(message = "password cannot be blank")
        @Length(min = 12, max = 64, message = "password cannot be shorter than 8 and longer than 20")
        String password,
        @Email
        String email) {
}
