package com.example.final_project.api.requests.budgets;

import com.example.final_project.domain.budgets.TypeOfBudget;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

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
