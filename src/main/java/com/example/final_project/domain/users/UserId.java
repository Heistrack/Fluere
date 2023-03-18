package com.example.final_project.domain.users;

public record UserId(String value) {
    public static UserId newId(String value){
        return new UserId(value);
    }
}
