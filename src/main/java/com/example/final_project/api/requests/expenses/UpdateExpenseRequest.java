package com.example.final_project.api.requests.expenses;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateExpenseRequest(
    @NotEmpty(message = "Title cannot be null")
    @Length(
            min = 3,
            max = 200,
            message = "Title cannot be shorter than 3 and longer than 200"
    )
    String title,
    @Positive(message = "Expense amount cannot be negative or zero")
    BigDecimal amount,

    String budgetId
) {
}
