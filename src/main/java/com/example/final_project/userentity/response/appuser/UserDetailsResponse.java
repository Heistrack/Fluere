package com.example.final_project.userentity.response.appuser;

import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.userentity.model.AppUser;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserDetailsResponse extends RepresentationModel<UserDetailsResponse>
        implements LinkableDTO {

    private final String login;
    private final UUID userId;
    private final String email;

    public static UserDetailsResponse fromDomain(AppUser user) {
        return new UserDetailsResponse(user.getLogin(), user.getUserId().id(), user.getEmail());
    }

    @Override
    public void addLink(Link link) {
        this.add(link);
    }

    @Override
    public String PathMessage() {
        return "user-response";
    }
}
