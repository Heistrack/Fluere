package com.example.final_project.api.responses.users.appusers;

import com.example.final_project.domain.users.AppUser;

import java.util.UUID;

public record UserDetailsResponse(String login, UUID userId, String email) {

    public static UserDetailsResponse fromDomain(AppUser user) {
        return new UserDetailsResponse(user.login(), user.userId().id(), user.email());
    }
}
