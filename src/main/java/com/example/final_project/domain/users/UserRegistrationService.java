package com.example.final_project.domain.users;

import org.apache.catalina.LifecycleState;

import java.util.List;

public interface UserRegistrationService {
    List<String> defaultRoles = List.of("USER");
    FluereAppUser registerNewUser(String username, String rawPassword, String email, List<String> roles);
}
