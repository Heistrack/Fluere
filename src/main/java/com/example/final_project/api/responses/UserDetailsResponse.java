package com.example.final_project.api.responses;

import com.example.final_project.domain.users.AppUser;

public record UserDetailsResponse(String username, String userId, String email) {

    public static UserDetailsResponse fromDomain(AppUser user) {
        return new UserDetailsResponse(user.name(), user.id().toString(), user.email());
    }
}
