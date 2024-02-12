package com.example.final_project.api.responses.authentications;

import lombok.Builder;

public record AuthResponseDTO(String token) {
    @Builder
    public AuthResponseDTO {
    }
}
