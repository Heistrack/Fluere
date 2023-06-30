package com.example.final_project.domain.users;

import org.springframework.security.core.context.SecurityContextHolder;

public enum UserContextProvider {
    INSTANCE;
    public static FluereAppUser getUserContext() {
        return (FluereAppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
