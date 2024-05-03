package com.example.final_project.security.response;

import com.example.final_project.userentity.service.AppUser;
import lombok.Builder;

public record RegisterResponseDTO(AppUser user, String token) {
    @Builder
    public RegisterResponseDTO{}
}
