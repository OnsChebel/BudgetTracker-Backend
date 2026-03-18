package com.ruse.budgettracker.repository;

import com.ruse.budgettracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository  extends JpaRepository<Budget, Long> {
}
