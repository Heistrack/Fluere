package com.example.final_project.userentity.service.user;

import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.exception.custom.UnableToCreateException;
import com.example.final_project.security.request.RegisterUserRequest;
import com.example.final_project.userentity.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    public <T extends LinkableDTO> PagedModel<T> getPagedModel(Page<T> linkableDTOs, Class<T> classCast,
                                                               Class<?> controllerClass
    ) {
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                linkableDTOs.getSize(),
                linkableDTOs.getNumber(),
                linkableDTOs.getTotalElements(),
                linkableDTOs.getTotalPages()
        );
        List<T> list = linkableDTOs.stream().toList();
        Link generalLink = linkTo(controllerClass).slash(linkableDTOs.getNumber() + 1).withSelfRel();
        list.forEach(dto -> dto.addLink(linkTo(controllerClass).slash(linkableDTOs.getNumber() + 1).slash(dto.PathMessage()).withSelfRel()));
        list.forEach(classCast::cast);
        return PagedModel.of(list, pageMetadata, generalLink);
    }
}
