package com.example.final_project.domain.users.admins;

import com.example.final_project.api.requests.users.admins.AdminEmailChangeRequest;
import com.example.final_project.api.requests.users.admins.AdminPasswordChangeRequest;
import com.example.final_project.api.requests.users.appusers.RegisterUserRequest;
import com.example.final_project.api.responses.users.admins.AdminOperationResponse;
import com.example.final_project.domain.users.appusers.AppUser;
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

    void removeAllUsers();

    void databaseRestart();

    AppUser patchUserEmail(AdminEmailChangeRequest request);

    AppUser resetUserPassword(AdminPasswordChangeRequest request);

    AdminOperationResponse authenticateTest(String login);
}
