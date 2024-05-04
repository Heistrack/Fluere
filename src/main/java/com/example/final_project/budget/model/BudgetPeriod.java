package com.example.final_project.budget.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BudgetPeriod {

    private LocalDate startTime;
    private LocalDate endTime;

    private BudgetPeriod(LocalDate startTime, LocalDate endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static BudgetPeriod newOf(LocalDate startTime, LocalDate endTime) {
        if (endTime.isBefore(startTime)) throw new IllegalArgumentException("Budget can't end before even start");

        return new BudgetPeriod(startTime, endTime);
    }
}
