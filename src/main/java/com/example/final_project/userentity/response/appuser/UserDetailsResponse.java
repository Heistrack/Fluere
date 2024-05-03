package com.example.final_project.userentity.response.appuser;

import com.example.final_project.userentity.service.AppUser;

import java.util.UUID;

public record UserDetailsResponse(String login, UUID userId, String email) {

    public static UserDetailsResponse fromDomain(AppUser user) {
        return new UserDetailsResponse(user.login(), user.userId().id(), user.email());
    }
}
