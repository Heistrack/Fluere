package com.example.final_project.domain.securities.jwtauth;

import lombok.Builder;

public record AuthenticationResponse(String token) {
    @Builder
    public AuthenticationResponse {
    }
}
