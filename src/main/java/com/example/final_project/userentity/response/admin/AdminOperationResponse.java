package com.example.final_project.userentity.response.admin;

import java.time.LocalDateTime;

public record AdminOperationResponse(String message, LocalDateTime timestamp) {
    public static AdminOperationResponse newOf(String message, LocalDateTime timestamp) {
        return new AdminOperationResponse(message, timestamp);
    }
}
