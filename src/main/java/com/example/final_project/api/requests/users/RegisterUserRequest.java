package com.example.final_project.api.requests.users;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
