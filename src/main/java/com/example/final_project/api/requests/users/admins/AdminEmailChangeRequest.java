package com.example.final_project.api.requests.users.admins;

import jakarta.validation.constraints.Email;

public record AdminEmailChangeRequest(
        String login,
        @Email(message = "This is not an email.")
        String newEmail
) {
}
