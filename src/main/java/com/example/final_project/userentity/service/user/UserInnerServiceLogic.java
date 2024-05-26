package com.example.final_project.userentity.service.user;

import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.security.request.RegisterUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;

public interface UserInnerServiceLogic {

    void emailAndLoginDuplicatesCheck(RegisterUserRequest request);

    <T extends LinkableDTO> PagedModel<T> getPagedModel(Page<T> linkableDTOs, Class<T> classCast,
                                                        Class<?> controllerClass
    );
}
