package com.example.final_project.api.requests.expenses;

import com.example.final_project.domain.expenses.TypeOfExpense;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Optional;

public record UpdateExpenseRequest(
        @NotEmpty(message = "Title cannot be null")
        @Length(
                min = 3,
                max = 200,
                message = "Title cannot be shorter than 3 and longer than 200"
        )
        String title,
        @Positive(message = "Expense amountNow cannot be negative or zero")
        BigDecimal amount,
        @NotEmpty(message = "Title cannot be null")
        @Length(
                min = 3,
                max = 200,
                message = "Title cannot be shorter than 3 and longer than 200"
        )
        @NotNull
        @NotEmpty
        String budgetId,
        Optional<TypeOfExpense> typeOfExpense
) {
}
