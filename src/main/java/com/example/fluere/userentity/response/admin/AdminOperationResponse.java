package com.example.fluere.userentity.response.admin;

import com.example.fluere.budget.model.LinkableDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class AdminOperationResponse extends RepresentationModel<AdminOperationResponse> implements LinkableDTO {

    private final String message;
    private final LocalDateTime timestamp;

    public static AdminOperationResponse newOf(String message, LocalDateTime timestamp) {
        return new AdminOperationResponse(message, timestamp);
    }

    @Override
    public void addLink(Link link) {
        this.add(link);
    }

    @Override
    public String PathMessage() {
        return "admin-response";
    }
}
