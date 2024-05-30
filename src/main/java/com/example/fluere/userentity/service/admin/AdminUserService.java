package com.example.fluere.userentity.service.admin;

import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.security.request.AuthenticationRequest;
import com.example.fluere.security.request.RegisterUserRequest;
import com.example.fluere.userentity.model.AppUser;
import com.example.fluere.userentity.request.admin.AdminEmailChangeRequest;
import com.example.fluere.userentity.request.admin.AdminPasswordChangeRequest;
import com.example.fluere.userentity.response.admin.AdminOperationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;
import java.util.UUID;

public interface AdminUserService {
    AppUser registerNewUser(RegisterUserRequest request);

    AppUser getByUserId(UUID userId);

    AppUser getFromToken(String userId);

    AppUser getUserByLogin(String login);

    Page<AppUser> getAllUsersByPage(Pageable pageable);

    List<AppUser> getAllUsersToList();

    AppUser getUserByEmail(String email);

    void removeUserByLogin(String login);

    void removeUserByUserId(UUID userId);

    void removeUserByEmail(String email);

    void removeAllUsers(AuthenticationRequest confirmation);

    void databaseRestart(AuthenticationRequest confirmation);

    AppUser patchUserEmail(AdminEmailChangeRequest request);

    AppUser resetUserPassword(AdminPasswordChangeRequest request);

    AdminOperationResponse authenticateTest(String login);

    <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast);

    <T extends LinkableDTO> PagedModel<T> getEntities(Page<T> linkableDTOs, Class<T> classCast);
}
