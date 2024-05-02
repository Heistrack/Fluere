package com.example.final_project.api.requests.users.admins;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminEmailChangeRequest(
        @NotBlank(message = "Login can not be blank.")
        String login,
        @Email(message = "This is not an email.")
        String newEmail
) {
}
