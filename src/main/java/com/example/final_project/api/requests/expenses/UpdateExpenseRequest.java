package com.example.final_project.api.requests.expenses;


import com.example.final_project.domain.expenses.TypeOfExpense;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record UpdateExpenseRequest(
        @Nullable
        @Length(min = 3, max = 200, message = "Title can not be shorter than 3 and longer than 200 characters.")
        String title,
        @Nullable
        @Positive(message = "Expense amount can not be negative or zero.")
        BigDecimal amount,
        @NotNull(message = "Expense's id can not be null.")
        @NotEmpty(message = "Expense's id can not be empty.")
        String expenseId,
        @Nullable
        TypeOfExpense typeOfExpense) {
}
