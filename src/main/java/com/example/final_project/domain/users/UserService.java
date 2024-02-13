package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;

import java.util.List;

public interface UserService {
    RegisterResponseDTO registerNewUser(RegisterUserRequest request);

    UserDetailsResponse findByUserId(String userId);

    AppUser findFromToken(String userId);

    AppUser findByLogin(String login);

    List<UserDetailsResponse> getAllUsers();

    AppUser findByEmail(String email);

    void removeUserByLogin(String login);

    void removeUserByUserId(String userId);

    void removeThemAll();
}
