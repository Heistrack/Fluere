package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.appusers.EmailChangeRequest;
import com.example.final_project.api.requests.users.appusers.PasswordChangeRequest;
import com.example.final_project.api.requests.users.appusers.RegisterUserRequest;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;

public interface UserService {
    RegisterResponseDTO registerNewUser(RegisterUserRequest request);

    void removeOwnAccount(UserIdWrapper userId);

    AppUser patchEmail(EmailChangeRequest request, UserIdWrapper userIdFromAuth);

    AppUser patchPassword(PasswordChangeRequest request, UserIdWrapper userIdFromAuth);
}
