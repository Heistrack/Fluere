package com.example.final_project.domain.securities.jwtauth;

import lombok.Builder;

public record RegisterRequest(String email, String name, String password) {
    @Builder
    public RegisterRequest {
    }
}
