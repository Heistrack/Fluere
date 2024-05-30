package com.example.fluere.security.request;

public record AuthenticationRequest(
        String login,
        String password
) {
}
