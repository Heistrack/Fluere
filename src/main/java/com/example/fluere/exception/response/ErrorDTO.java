package com.example.fluere.exception.response;

import org.springframework.http.HttpStatus;

public record ErrorDTO(String message, HttpStatus status, String timestamp) {
    public static ErrorDTO newOf(String message, HttpStatus status, String timestamp) {
        return new ErrorDTO(message, status, timestamp);
    }
}
