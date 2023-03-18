package com.example.final_project.api.responses;

import com.example.final_project.domain.users.FluereAppUser;

public record UserDetailsResponse(String username, String userId, String email) {

    public static UserDetailsResponse fromDomain(FluereAppUser user){
        return new UserDetailsResponse(user.userName(), user.userId().value(), user.email());
    }
}
