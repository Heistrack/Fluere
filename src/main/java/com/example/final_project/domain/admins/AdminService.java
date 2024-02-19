package com.example.final_project.domain.admins;

import com.example.final_project.api.requests.users.EmailChangeRequest;
import com.example.final_project.api.requests.users.PasswordChangeRequest;
import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.UserIdWrapper;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    AppUser registerNewUser(RegisterUserRequest request);

    UserDetailsResponse findByUserId(UUID userId);

    AppUser findFromToken(String userId);

    AppUser findByLogin(String login);

    List<UserDetailsResponse> getAllUsers();

    AppUser findUserByEmail(String email);

    void removeUserByLogin(String login);

    void removeUserByUserId(UUID userId);

    void removeAllUsers();
    void removeThemAll();

    AppUser patchUserEmail(EmailChangeRequest request, UserIdWrapper userIdFromAuth);

    AppUser patchUserPassword(PasswordChangeRequest request, UserIdWrapper userIdFromAuth);
}
