package com.example.final_project.userentity.service.admin;

import com.example.final_project.userentity.request.admin.AdminEmailChangeRequest;
import com.example.final_project.userentity.request.admin.AdminPasswordChangeRequest;
import com.example.final_project.userentity.request.appuser.AuthenticationRequest;
import com.example.final_project.userentity.request.appuser.RegisterUserRequest;
import com.example.final_project.userentity.response.admin.AdminOperationResponse;
import com.example.final_project.userentity.service.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminService {
    AppUser registerNewUser(RegisterUserRequest request);

    AppUser findByUserId(UUID userId);

    AppUser findFromToken(String userId);

    AppUser findUserByLogin(String login);

    Page<AppUser> getAllUsersByPage(Pageable pageable);

    AppUser findUserByEmail(String email);

    void removeUserByLogin(String login);

    void removeUserByUserId(UUID userId);

    void removeUserByEmail(String email);

    void removeAllUsers(AuthenticationRequest confirmation);

    void databaseRestart(AuthenticationRequest confirmation);

    AppUser patchUserEmail(AdminEmailChangeRequest request);

    AppUser resetUserPassword(AdminPasswordChangeRequest request);

    AdminOperationResponse authenticateTest(String login);
}
