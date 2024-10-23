package com.example.fluere.userentity.service.user;

import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.security.request.RegisterUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;

public interface UserInnerServiceLogic {

    void emailAndLoginDuplicatesCheck(RegisterUserRequest request);

    <T extends LinkableDTO> PagedModel<T> getPagedModel(Page<T> linkableDTOs, Class<T> classCast,
                                                        Class<?> controllerClass
    );
}
