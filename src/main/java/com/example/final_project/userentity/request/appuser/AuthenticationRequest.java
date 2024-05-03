package com.example.final_project.userentity.request.appuser;

public record AuthenticationRequest(
        String login,
        String password
) {
}
