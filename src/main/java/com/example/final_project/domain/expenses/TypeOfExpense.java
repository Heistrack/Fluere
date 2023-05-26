package com.example.final_project.domain.expenses;

import lombok.ToString;

@ToString
public enum TypeOfExpense {
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

    TypeOfExpense(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
