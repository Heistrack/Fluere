package com.example.final_project.userentity.service.user;

import com.example.final_project.security.request.AuthenticationRequest;
import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.security.response.RegisterResponseDTO;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.request.appuser.EmailChangeRequest;
import com.example.final_project.userentity.request.appuser.PasswordChangeRequest;
import org.springframework.security.core.Authentication;

public interface UserService {
    RegisterResponseDTO registerNewUser(RegisterUserRequest request);

    AppUser getUserDetailsFromToken(Authentication authentication);

    void removeOwnAccount(AuthenticationRequest confirmation, Authentication authentication);

    AppUser patchEmail(EmailChangeRequest request, Authentication authentication);

    AppUser patchPassword(PasswordChangeRequest request, Authentication authentication);
}
