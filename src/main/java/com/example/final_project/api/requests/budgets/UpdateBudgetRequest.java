package com.example.final_project.api.requests.budgets;

import com.example.final_project.domain.budgets.BudgetType;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record UpdateBudgetRequest(
        @Size(min = 3, message = "Title can not be shorter than 3 characters")
        @Size(max = 200, message = "Title can not be longer than 200 characters")
        String title,
        @Positive(message = "Budget limit can not be negative or zero.")
        BigDecimal limit,
        @Nullable
        BudgetType budgetType,
        @Positive(message = "Max single expense can not be negative or zero.")
        BigDecimal maxSingleExpense
) {
}
