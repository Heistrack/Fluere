package com.example.final_project.api.auth;

public record Token(String value) {
    public static Token newOf(String value) {
        return new Token(value);
    }
}
