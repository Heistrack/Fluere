package com.example.final_project.api.requests.users.admins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminPasswordChangeRequest(
        @NotBlank(message = "Login can not be blank.")
        String login,
        @NotBlank(message = "Password can not be blank.")
        //TODO CHANGE PASSWORD SIZE - during dev make it small
        @Size(min = 1, max = 64, message = "Password can not be shorter than 8 and longer than 64 characters.")
        String newPassword
) {

}
