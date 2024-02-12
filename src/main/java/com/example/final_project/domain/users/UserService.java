package com.example.final_project.domain.users;

import java.util.List;

public interface UserService {
    //TODO CREATE INTERFACE FOR USER SERVICE INTERFACE
    List<String> defaultRoles = List.of("USER");

    AppUser registerNewUser(String login, String rawPassword, String email, List<String> roles);

    AppUser findUserByUsername(String username);
}
