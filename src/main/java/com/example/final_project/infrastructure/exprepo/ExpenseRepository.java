package com.example.final_project.infrastructure.exprepo;

import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface ExpenseRepository extends MongoRepository<Expense,ExpenseId> {


    Optional<Expense> findExpenseByExpenseId(ExpenseId expenseId);





}
