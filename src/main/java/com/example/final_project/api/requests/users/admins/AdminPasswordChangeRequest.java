package com.example.final_project.api.requests.users.admins;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record AdminPasswordChangeRequest(
        @NotBlank(message = "Login can not be blank.")
        String login,
        @NotBlank(message = "Password can not be blank.")
        //TODO CHANGE PASSWORD SIZE - during dev make it small
        @Length(min = 1, max = 64, message = "Password can not be shorter than 8 and longer than 64 characters.")
        String newPassword
) {
}
