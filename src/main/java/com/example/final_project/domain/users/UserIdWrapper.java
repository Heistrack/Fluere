package com.example.final_project.domain.users;

import java.util.UUID;

public record UserIdWrapper(UUID id) {
    public static UserIdWrapper newOf(UUID id) {
        return new UserIdWrapper(id);
    }

    public static UserIdWrapper newFromString(String rawStringUserId) {
        return new UserIdWrapper(UUID.fromString(rawStringUserId));
    }
}
