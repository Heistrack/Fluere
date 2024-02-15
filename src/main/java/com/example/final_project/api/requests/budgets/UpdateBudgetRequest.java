package com.example.final_project.api.requests.budgets;

import com.example.final_project.domain.budgets.TypeOfBudget;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record UpdateBudgetRequest(
        @Nullable
        @NotEmpty(message = "Budget title can not be empty.")
        @Length(min = 3, max = 200, message = "Title can not be shorter than 3 and longer than 200 characters.")
        String title,
        @Positive(message = "Budget limit can not be negative or zero.")
        @Nullable
        BigDecimal limit,
        @Nullable
        TypeOfBudget typeOfBudget,
        @Positive(message = "Single expense limit can not be negative or zero.")
        @Nullable
        BigDecimal maxSingleExpense
) {
}
