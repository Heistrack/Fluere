package com.example.final_project.api.requests.budgets;

import com.example.final_project.domain.budgets.TypeOfBudget;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record RegisterBudgetRequest(
        @NotNull(message = "Budget title can not be null.")
        @NotEmpty(message = "Budget title can not be empty.")
        @Length(min = 3, max = 200, message = "Title can not be shorter than 3 and longer than 200 characters.")
        String title,
        @NotNull(message = "Budget limit can not be null.")
        @Positive(message = "Budget limit can not be negative or zero.")
        BigDecimal limit,
        @NotNull(message = "Type of budget can not be null.")
        TypeOfBudget typeOfBudget,
        @NotNull(message = "Single expense limit can not be null.")
        @Positive(message = "Single expense limit can not be negative or zero.")
        BigDecimal maxSingleExpense
) {
}
