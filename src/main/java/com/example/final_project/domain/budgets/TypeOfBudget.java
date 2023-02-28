package com.example.final_project.domain.budgets;

public enum TypeOfBudget {
    HALF("can be exceeded to half of total value", 1.5),
    FULL("can be exceeded with no limit", Double.MAX_VALUE),
    STRICT("can't be exceeded", 1.0);

    private final String title;
    private final Double value;

    TypeOfBudget(String title, Double value){
        this.title = title;
        this.value = value;
    }
}
