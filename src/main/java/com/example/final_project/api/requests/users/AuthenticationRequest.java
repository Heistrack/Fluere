package com.example.final_project.api.requests.users;

import lombok.Builder;

public record AuthenticationRequest(String login, String password) {
    @Builder
    public AuthenticationRequest {
    }
}
