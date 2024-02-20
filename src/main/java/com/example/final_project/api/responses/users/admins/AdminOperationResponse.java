package com.example.final_project.api.responses.users.admins;

import java.time.LocalDateTime;

public record AdminOperationResponse(String message, LocalDateTime timestamp) {
    public static AdminOperationResponse newOf(String message, LocalDateTime timestamp) {
        return new AdminOperationResponse(message, timestamp);
    }
}
