package com.example.final_project.api.responses.authentications;

import com.example.final_project.domain.users.appusers.AppUser;
import lombok.Builder;

public record RegisterResponseDTO(AppUser user, String token) {
    @Builder
    public RegisterResponseDTO{}
}
