package com.ruse.budgettracker.repository;

import com.ruse.budgettracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND UPPER(t.type) = 'INCOME'")
    Double getTotalIncomeByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND UPPER(t.type) = 'EXPENSE'")
    Double getTotalExpenseByUserId(@Param("userId") Long userId);
}
