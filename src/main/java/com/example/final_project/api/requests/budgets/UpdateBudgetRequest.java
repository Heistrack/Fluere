package com.example.final_project.api.requests.budgets;

import com.example.final_project.domain.budgets.TypeOfBudget;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateBudgetRequest(

        @NotEmpty(message = "Budget title cannot be empty")
        @Length(
                min = 3,
                max = 200,
                message = "Title cannot be shorter than 3 and longer than 200"
        )
        String title,

        @Positive(message = "Budget limit cannot be negative or zero")
        BigDecimal limit,

        TypeOfBudget typeOfBudget,
        @Positive(message = "single expense limit cannot be negative or zero")
        BigDecimal maxSingleExpense

) {
}
