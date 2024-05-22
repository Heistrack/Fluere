package com.example.final_project.userentity.service.user;

import com.example.final_project.exception.custom.UnableToCreateException;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.response.ExpenseResponseDto;
import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class DefaultInnerUserServiceLogic implements UserInnerServiceLogic {

    private final AppUserRepository userRepository;

    @Override
    public void emailAndLoginDuplicatesCheck(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UnableToCreateException("User's email is already occupied!");
        }
        if (userRepository.existsByLogin(request.login())) {
            throw new UnableToCreateException("User's login is already occupied!");
        }
    }

    @Override
    public EntityModel<AppUser> getEntityModelFromLink(Link link, AppUser user) {
        return EntityModel.of(user.add(link));
    }

    @Override
    public PagedModel<AppUser> getPagedModel(Link generalLink, Class<?> controller, Page<AppUser> users) {
        List<AppUser> list = users.stream().toList();
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(users.getSize(), users.getNumber(),
                                                                           users.getTotalElements(),
                                                                           users.getTotalPages());
        list.forEach(dto -> dto.add(linkTo(controller).slash(dto.getUserId().id()).withSelfRel()));
        return PagedModel.of(list, pageMetadata, generalLink);
    }
}
