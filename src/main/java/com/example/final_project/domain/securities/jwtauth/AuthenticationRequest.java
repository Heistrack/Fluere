package com.example.final_project.domain.securities.jwtauth;

import lombok.Builder;

public record AuthenticationRequest(String email, String password) {
    @Builder
    public AuthenticationRequest {
    }
}
