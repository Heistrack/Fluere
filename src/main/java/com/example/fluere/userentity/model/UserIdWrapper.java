package com.example.fluere.userentity.model;

import java.util.UUID;

public record UserIdWrapper(UUID id) {
    public static UserIdWrapper newOf(UUID id) {
        return new UserIdWrapper(id);
    }

    public static UserIdWrapper newFromString(String rawStringAppUserId) {
        return new UserIdWrapper(UUID.fromString(rawStringAppUserId));
    }
}
