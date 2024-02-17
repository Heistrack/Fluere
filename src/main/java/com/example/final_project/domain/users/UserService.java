package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.EmailChangeRequest;
import com.example.final_project.api.requests.users.PasswordChangeRequest;
import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.api.responses.authentications.RegisterResponseDTO;
import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetIdWrapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    RegisterResponseDTO registerNewUser(RegisterUserRequest request);

    UserDetailsResponse findByUserId(UUID userId);

    AppUser findFromToken(String userId);

    AppUser findByLogin(String login);

    List<UserDetailsResponse> getAllUsers();

    AppUser findByEmail(String email);

    void removeUserByLogin(String login);

    void removeUserByUserId(UUID userId);

    void removeThemAll();

    AppUser patchEmail(EmailChangeRequest request, UserIdWrapper userIdFromAuth);

    AppUser patchPassword(PasswordChangeRequest request, UserIdWrapper userIdFromAuth);
}
