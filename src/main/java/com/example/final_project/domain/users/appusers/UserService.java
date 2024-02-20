package com.example.final_project.domain.users.appusers;

import com.example.final_project.api.requests.users.appusers.EmailChangeRequest;
import com.example.final_project.api.requests.users.appusers.PasswordChangeRequest;
import com.example.final_project.api.requests.users.appusers.RegisterUserRequest;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import org.springframework.security.core.Authentication;

public interface UserService {
    RegisterResponseDTO registerNewUser(RegisterUserRequest request);

    AppUser getUserDetailsFromToken(Authentication authentication);

    void removeOwnAccount(Authentication authentication);

    AppUser patchEmail(EmailChangeRequest request, Authentication authentication);

    AppUser patchPassword(PasswordChangeRequest request, Authentication authentication);
}
