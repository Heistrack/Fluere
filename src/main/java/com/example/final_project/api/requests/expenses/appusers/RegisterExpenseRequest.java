package com.example.final_project.api.requests.expenses.appusers;


import com.example.final_project.domain.expenses.ExpenseType;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record RegisterExpenseRequest(
        @Size(min = 3, max = 200, message = "Title can not be shorter than 3 and longer than 200 characters.")
        String title,
        @NotNull(message = "Expense's amount can not be null.")
        @Positive(message = "Expense's amount can not be negative or zero.")
        BigDecimal amount,
        @NotBlank(message = "Budget's ID can not be null or blank.")
        String budgetId,
        @Nullable
        ExpenseType expenseType,
        @Size(max = 8000, message = "The description can not be more 8.000 characters.")
        String description
) {
}
