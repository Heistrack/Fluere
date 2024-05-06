package com.example.final_project.security.request;

public record AuthenticationRequest(
        String login,
        String password
) {
}
