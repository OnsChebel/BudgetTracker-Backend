package com.ruse.budgettracker.repository;

import com.ruse.budgettracker.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
