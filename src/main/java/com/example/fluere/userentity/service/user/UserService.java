package com.example.fluere.userentity.service.user;

import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.security.request.AuthenticationRequest;
import com.example.fluere.security.request.RegisterUserRequest;
import com.example.fluere.security.response.RegisterResponseDTO;
import com.example.fluere.userentity.model.AppUser;
import com.example.fluere.userentity.request.appuser.EmailChangeRequest;
import com.example.fluere.userentity.request.appuser.PasswordChangeRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;

public interface UserService {
    RegisterResponseDTO registerNewUser(RegisterUserRequest request);

    AppUser getUserDetailsFromToken(Authentication authentication);

    void removeOwnAccount(AuthenticationRequest confirmation, Authentication authentication);

    AppUser patchEmail(EmailChangeRequest request, Authentication authentication);

    AppUser patchPassword(PasswordChangeRequest request, Authentication authentication);

    <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast);
}
