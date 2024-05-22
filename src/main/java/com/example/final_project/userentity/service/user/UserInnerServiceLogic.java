package com.example.final_project.userentity.service.user;

import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.userentity.model.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

public interface UserInnerServiceLogic {

    void emailAndLoginDuplicatesCheck(RegisterUserRequest request);

    EntityModel<AppUser> getEntityModelFromLink(Link link, AppUser user);

    PagedModel<AppUser> getPagedModel(Link generalLink, Class<?> controller, Page<AppUser> users);
}
