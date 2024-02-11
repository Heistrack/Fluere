package com.example.final_project.domain.users;

import java.util.UUID;

public record UserId(UUID userId) {
    public static UserId newId(UUID id) {
        return new UserId(id);
    }
}
