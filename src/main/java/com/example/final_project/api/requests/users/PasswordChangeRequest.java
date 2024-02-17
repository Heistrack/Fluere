package com.example.final_project.api.requests.users;

import lombok.Builder;

public record PasswordChangeRequest(AuthenticationRequest auth, String firstPasswordAttempt, String secondPasswordAttempt) {
    @Builder
    public PasswordChangeRequest {}
}
