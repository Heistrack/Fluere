package com.example.final_project.userentity.response.appuser;

import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.model.Role;
import com.example.final_project.userentity.model.UserIdWrapper;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDetailsResponseDTO extends RepresentationModel<UserDetailsResponseDTO>
        implements LinkableDTO {

    private final UUID userId;
    private final String login;
    private final String email;
    private final String password;
    @Enumerated(EnumType.STRING)
    private final Role role;
    private final Boolean enabled;
    private final LocalDateTime creationTime;

    public static UserDetailsResponseDTO fromDomain(AppUser user) {
        return new UserDetailsResponseDTO(
                user.getUserId().id(),
                user.getLogin(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getEnabled(),
                user.getCreationTime()
        );
    }

    @Override
    public void addLink(Link link) {
        this.add(link);
    }

    @Override
    public String PathMessage() {
        return userId.toString();
    }
}
