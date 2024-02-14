package com.example.final_project.domain.users;

import java.util.UUID;

public record UserIdWrapper(UUID userId) {
    public static UserIdWrapper newId(UUID id) {
        return new UserIdWrapper(id);
    }

    public static UserIdWrapper newFromString(String id) {
        return new UserIdWrapper(UUID.fromString(id));
    }
}
