package com.example.final_project.api.requests.budgets.appusers;

import com.example.final_project.domain.budgets.appusers.BudgetType;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PatchBudgetRequest(
        @NotBlank(message = "Budget's ID can not be null or empty.")
        String budgetId,
        @Size(min = 3, message = "Title can not be shorter than 3 characters")
        @Size(max = 200, message = "Title can not be longer than 200 characters")
        String title,
        @Positive(message = "Budget's limit can not be negative or zero.")
        BigDecimal limit,
        @Nullable
        BudgetType budgetType,
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
