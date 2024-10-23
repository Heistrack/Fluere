package com.example.fluere.userentity.response.appuser;

import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.userentity.model.AppUser;
import com.example.fluere.userentity.model.Role;
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
