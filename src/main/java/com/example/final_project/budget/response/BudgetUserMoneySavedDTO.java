package com.example.final_project.budget.response;

import com.example.final_project.budget.model.LinkableDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class BudgetUserMoneySavedDTO extends RepresentationModel<BudgetUserMoneySavedDTO> implements LinkableDTO {

    private final String userId;
    private final BigDecimal moneySaved;

    public static BudgetUserMoneySavedDTO newOf(String userId, BigDecimal moneySaved) {
        return new BudgetUserMoneySavedDTO(userId, moneySaved);
    }

    @Override
    public void addLink(Link link) {
        this.add(link);
    }

    @Override
    public String PathMessage() {
        return userId;
    }
}
