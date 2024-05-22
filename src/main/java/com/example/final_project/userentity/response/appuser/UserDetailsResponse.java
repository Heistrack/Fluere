package com.example.final_project.userentity.response.appuser;

import com.example.final_project.userentity.model.AppUser;

import java.util.UUID;

public record UserDetailsResponse(String login, UUID userId, String email) {

    public static UserDetailsResponse fromDomain(AppUser user) {
        return new UserDetailsResponse(user.getLogin(), user.getUserId().id(), user.getEmail());
    }
}
