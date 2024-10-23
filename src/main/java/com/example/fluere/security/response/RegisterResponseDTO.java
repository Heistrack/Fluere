package com.example.fluere.security.response;

import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.userentity.model.AppUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;


@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterResponseDTO extends RepresentationModel<RegisterResponseDTO> implements LinkableDTO {

    private final AppUser user;
    private final String token;

    public static RegisterResponseDTO newOf(AppUser user, String token) {
        return new RegisterResponseDTO(user, token);
    }

    @Override
    public void addLink(Link link) {
        this.add(link);
    }

    @Override
    public String PathMessage() {
        return user.getUserId().id().toString();
    }
}
