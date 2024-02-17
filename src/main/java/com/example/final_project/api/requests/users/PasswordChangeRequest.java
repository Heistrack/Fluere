package com.example.final_project.api.requests.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

public record PasswordChangeRequest(
        AuthenticationRequest auth,
        @NotNull(message = "Password can not be null.")
        @NotBlank(message = "Password can not be blank.")
        //TODO CHANGE PASSWORD SIZE - during dev make it small
        @Length(min = 1, max = 64, message = "Password can not be shorter than 8 and longer than 64 characters.")
        String firstPasswordAttempt,
        @NotNull(message = "Password can not be null.")
        @NotBlank(message = "Password can not be blank.")
        //TODO CHANGE PASSWORD SIZE - during dev make it small
        @Length(min = 1, max = 64, message = "Password can not be shorter than 8 and longer than 64 characters.")
        String secondPasswordAttempt
) {
    @Builder
    public PasswordChangeRequest {
    }
}
