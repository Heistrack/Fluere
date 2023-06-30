package com.example.final_project.domain.users;

import java.util.List;

public interface UserService {
    List<String> defaultRoles = List.of("USER");

    FluereAppUser registerNewUser(String username, String rawPassword, String email, List<String> roles);

    FluereAppUser findUserByUsername(String username);
}
