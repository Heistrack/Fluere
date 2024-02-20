package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.appusers.EmailChangeRequest;
import com.example.final_project.api.requests.users.appusers.PasswordChangeRequest;
import com.example.final_project.api.requests.users.appusers.RegisterUserRequest;
import com.example.final_project.api.responses.users.appusers.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    RegisterResponseDTO registerNewUser(RegisterUserRequest request);

    UserDetailsResponse findByUserId(UUID userId);

    AppUser findFromToken(String userId);

    AppUser findByLogin(String login);

    List<UserDetailsResponse> getAllUsers();

    AppUser findByEmail(String email);

    void removeMyAccount(String login);

    void removeUserByUserId(UUID userId);

    void removeThemAll();

    AppUser patchEmail(EmailChangeRequest request, UserIdWrapper userIdFromAuth);

    AppUser patchPassword(PasswordChangeRequest request, UserIdWrapper userIdFromAuth);
}
