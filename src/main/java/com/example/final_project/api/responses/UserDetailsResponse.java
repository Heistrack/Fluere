package com.example.final_project.api.responses;

import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.UserIdWrapper;

public record UserDetailsResponse(String login, UserIdWrapper userId, String email) {

    public static UserDetailsResponse fromDomain(AppUser user) {
        return new UserDetailsResponse(user.login(), user.userId(), user.email());
    }
}
