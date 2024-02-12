package com.example.final_project.api.responses;

import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.UserId;

public record UserDetailsResponse(String login, UserId userId, String email) {

    public static UserDetailsResponse fromDomain(AppUser user) {
        return new UserDetailsResponse(user.login(), user.id(), user.email());
    }
}
