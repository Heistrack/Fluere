package com.example.fluere.expense.request.admin;

import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.model.ExpenseType;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdminRegisterExpenseRequest(
        @NotBlank(message = "Budget's ID can not be null or blank.")
        String budgetId,
        @NotBlank(message = "User's ID can't be blank or null.")
        String userId,
        @Size(min = 3, max = 200, message = "Title can not be shorter than 3 and longer than 200 characters.")
        String title,
        @NotNull(message = "Expense's amount can not be null.")
        @Positive(message = "Expense's amount can not be negative or zero.")
        BigDecimal amount,
        @NotNull(message = "Expense must have defined currency")
        MKTCurrency currency,
        @Nullable
        ExpenseType expenseType,
        @Size(max = 8000, message = "The expense's description can not be more than 8.000 characters.")
        String description
) {
}
