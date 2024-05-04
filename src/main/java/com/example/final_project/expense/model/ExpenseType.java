package com.example.final_project.expense.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum ExpenseType {
    ENTERTAINMENT("Entertainment"),
    ACCOMMODATION("Accommodation"),
    OTHER_BILLS("Other bills"),
    FOOD("Food"),
    TRANSPORT("Transport"),
    CLOTHES("Clothes"),
    HEALTH_AND_CHEMISTRY("Health and chemistry"),
    OTHERS("Others"),
    NO_CATEGORY("No category");

    private final String title;

    ExpenseType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
