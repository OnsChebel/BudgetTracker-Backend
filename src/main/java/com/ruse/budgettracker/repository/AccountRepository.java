package com.ruse.budgettracker.repository;

import com.ruse.budgettracker.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
