package com.example.final_project.security.response;

import com.example.final_project.budget.model.LinkableDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthResponseDTO extends RepresentationModel<AuthResponseDTO> implements LinkableDTO {

    private final UUID userId;
    private final String token;

    public static AuthResponseDTO newOf(UUID userId, String token) {
        return new AuthResponseDTO(userId, token);
    }

    @Override
    public void addLink(Link link) {
        this.add(link);
    }

    @Override
    public String PathMessage() {
        return "auth";
    }
}
