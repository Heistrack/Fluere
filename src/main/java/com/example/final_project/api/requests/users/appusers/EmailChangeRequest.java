package com.example.final_project.api.requests.users.appusers;

import jakarta.validation.constraints.Email;

public record EmailChangeRequest(
        AuthenticationRequest auth,
        @Email(message = "This is not an email.")
        String newEmail
) {
}
