package com.example.final_project.api.requests.users.appusers;

public record AuthenticationRequest(
        String login,
        String password
) {
}
