package com.example.final_project.security.response;

import lombok.Builder;

public record AuthResponseDTO(String token) {
    @Builder
    public AuthResponseDTO {
    }
}
