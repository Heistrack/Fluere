package com.example.final_project.api.requests.budgets.appusers;

import com.example.final_project.domain.budgets.appusers.BudgetType;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateBudgetRequest(
        @NotBlank(message = "Budget's ID can not be null or empty.")
        String budgetId,
        @Size(min = 3, max = 200, message = "Title can not be shorter than 3 and longer than 200 characters.")
        String title,
        @NotNull(message = "Budget's limit can not be null.")
        @Positive(message = "Budget's limit can not be negative or zero.")
        BigDecimal limit,
        @NotNull(message = "Budget's type can not be null.")
        BudgetType budgetType,
        @NotNull(message = "Budget's max single expense can not be null.")
        @Positive(message = "Budget's max single expense can not be negative or zero.")
        BigDecimal maxSingleExpense,
        @Nullable
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate budgetStart,
        @Nullable
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate budgetEnd,
        @Size(max = 8000, message = "The budget's description can not be more than 8.000 characters.")
        String description
) {
}
