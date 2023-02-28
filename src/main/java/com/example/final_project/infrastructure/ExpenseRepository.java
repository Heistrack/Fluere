package com.example.final_project.infrastructure;

import com.example.final_project.domain.Expense;
import com.example.final_project.domain.ExpenseId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface ExpenseRepository extends MongoRepository<Expense,ExpenseId> {


    Optional<Expense> findExpenseByExpenseId(ExpenseId expenseId);




}
