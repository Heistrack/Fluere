package com.example.final_project.userentity.service.user;

import com.example.final_project.userentity.request.appuser.AuthenticationRequest;
import com.example.final_project.userentity.request.appuser.EmailChangeRequest;
import com.example.final_project.userentity.request.appuser.PasswordChangeRequest;
import com.example.final_project.userentity.request.appuser.RegisterUserRequest;
import com.example.final_project.security.response.RegisterResponseDTO;
import com.example.final_project.userentity.service.AppUser;
import org.springframework.security.core.Authentication;

public interface UserService {
    RegisterResponseDTO registerNewUser(RegisterUserRequest request);

    AppUser getUserDetailsFromToken(Authentication authentication);

    void removeOwnAccount(AuthenticationRequest confirmation, Authentication authentication);

    AppUser patchEmail(EmailChangeRequest request, Authentication authentication);

    AppUser patchPassword(PasswordChangeRequest request, Authentication authentication);
}
