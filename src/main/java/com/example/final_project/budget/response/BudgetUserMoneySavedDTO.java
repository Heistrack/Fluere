package com.example.final_project.budget.response;

import com.example.final_project.budget.model.LinkableDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class BudgetUserMoneySavedDTO extends RepresentationModel<BudgetUserMoneySavedDTO> implements LinkableDTO {

    private final UUID userId;
    private final BigDecimal moneySaved;

    public static BudgetUserMoneySavedDTO newOf(UUID userId, BigDecimal moneySaved) {
        return new BudgetUserMoneySavedDTO(userId, moneySaved);
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
