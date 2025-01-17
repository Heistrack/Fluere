package com.example.fluere.expense.request.appuser;


import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.model.ExpenseType;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PatchExpenseRequest(
        @NotBlank(message = "Expense's ID can not be null or blank.")
        String expenseId,
        @Size(min = 3, message = "Title can not be shorter than 3 characters")
        @Size(max = 200, message = "Title can not be longer than 200 characters")
        String title,
        @Positive(message = "Expense's amount can not be negative or zero.")
        BigDecimal amount,
        @Nullable
        MKTCurrency currency,
        @Nullable
        ExpenseType expenseType,
        @Size(max = 8000, message = "The expense's description can not be more than 8.000 characters.")
        String description
) {
}
