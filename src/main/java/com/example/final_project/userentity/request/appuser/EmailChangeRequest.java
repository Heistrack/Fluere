package com.example.final_project.userentity.request.appuser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailChangeRequest(
        @Email(message = "This is not an email.")
        String newEmail,
        @NotBlank(message = "Password can not be blank.")
        String password
) {
}
