package com.ruse.budgettracker.repository;

import com.ruse.budgettracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository  extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
}
