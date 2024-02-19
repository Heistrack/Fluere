package com.example.final_project.api.requests.expenses;


import com.example.final_project.domain.expenses.ExpenseType;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateExpenseRequest(

        @Size(min = 3, message = "Title can not be shorter than 3 characters")
        @Size(max = 200, message = "Title can not be longer than 200 characters")
        String title,
        @Positive(message = "Expense amount can not be negative or zero.")
        BigDecimal amount,
        @NotBlank(message = "Expense's id can not be null or blank.")
        String expenseId,
        @Nullable
        ExpenseType expenseType) {
}
