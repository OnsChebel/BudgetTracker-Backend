package com.ruse.budgettracker.repository;

import com.ruse.budgettracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
