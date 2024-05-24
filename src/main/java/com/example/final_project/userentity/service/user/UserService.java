package com.example.final_project.userentity.service.user;

import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.security.request.AuthenticationRequest;
import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.security.response.RegisterResponseDTO;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.request.appuser.EmailChangeRequest;
import com.example.final_project.userentity.request.appuser.PasswordChangeRequest;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.Authentication;

public interface UserService {
    RegisterResponseDTO registerNewUser(RegisterUserRequest request);

    AppUser getUserDetailsFromToken(Authentication authentication);

    void removeOwnAccount(AuthenticationRequest confirmation, Authentication authentication);

    AppUser patchEmail(EmailChangeRequest request, Authentication authentication);

    AppUser patchPassword(PasswordChangeRequest request, Authentication authentication);

    <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast);

    <T extends LinkableDTO> PagedModel<T> getEntities(Page<T> linkableDTOs, Class<T> classCast);
}
