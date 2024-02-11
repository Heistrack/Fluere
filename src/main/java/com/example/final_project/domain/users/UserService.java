package com.example.final_project.domain.users;

import java.util.List;

public interface UserService {
    List<String> defaultRoles = List.of("USER");

    AppUser registerNewUser(String username, String rawPassword, String email, List<String> roles);

    AppUser findUserByUsername(String username);
}
